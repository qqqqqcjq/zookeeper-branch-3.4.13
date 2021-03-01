package com.luban.client.curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/8/21 17:32
 * @author chengjiaqing
 * @version : 0.1
 */

//使用锁机制，谁抢到锁谁就是Leader
//这里的leader也就是一个称呼，意思就是对应的client被选中了，至于选中它做什么，就是客户端自己的事情了,比如作为主从中的主
//这种方式就是简单指定一个client, 并不会使用zookeeper的投票算法等
public class LeaderSelectorExample {

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderSelector> leaderSelectors = Lists.newArrayList();

        for(int i = 0; i <10 ; i++){
            CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.43.122:2181",new RetryNTimes(3,1000));
            clients.add(client);
            client.start();

            LeaderSelector leaderSelector = new LeaderSelector(client, "/LeaderSelector", new LeaderSelectorListener() {
                @Override
                //这个方法执行完后就释放锁了，其他客户端可能抢到锁变为Leader
                public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                    System.out.println("当前Leader是" + client);
                    TimeUnit.SECONDS.sleep(5);
                }
                @Override
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

                }
            });
            leaderSelector.start();
            leaderSelectors.add(leaderSelector);
        }


        System.in.read();
    }
}