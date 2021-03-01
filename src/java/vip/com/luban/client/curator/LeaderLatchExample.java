package com.luban.client.curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.RetryNTimes;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/8/21 17:01
 * @author chengjiaqing
 * @version : 0.1
 */ 
 

//使用临时顺序节点， 其中最小的节点对应的客户端作为leader,
//这里的leader也就是一个称呼，意思就是对应的client被选中了，至于选中它做什么，就是客户端自己的事情了,比如作为主从中的主
//这种方式就是简单指定一个client, 并不会使用zookeeper的投票算法等
public class LeaderLatchExample {
    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderLatch> leaderLatches = Lists.newArrayList();

        for(int i = 0; i <10 ; i++){
            CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.43.122:2181",new RetryNTimes(3,1000));
            clients.add(client);
            client.start();

            LeaderLatch leaderLatch = new LeaderLatch(client,"/LeaderLatch", "client#"+i);
            leaderLatches.add(leaderLatch);
            leaderLatch.start();
        }

        TimeUnit.SECONDS.sleep(5);
        for (LeaderLatch leaderLatch:leaderLatches){
            if(leaderLatch.hasLeadership()){
                System.out.println("当前Leader是"+leaderLatch.getId());
                break;
            }
        }

        System.in.read();
    }
}