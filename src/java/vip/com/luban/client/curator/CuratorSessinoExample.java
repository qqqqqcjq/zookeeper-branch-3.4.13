package com.luban.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;

/**
 * @date 2020/8/21 16:51
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class CuratorSessinoExample {
    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.43.122:2181",5000,5000,new RetryNTimes(3,1000));
        client.start();

        //注册一个监听网络状态的监听器
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if(connectionState ==ConnectionState.LOST){
                    try {
                        //blockUntilConnectedOrTimedOut 阻塞到网络连通或者超时
                        if(client.getZookeeperClient().blockUntilConnectedOrTimedOut()){
                            //然后我们进行一些修复处理
                            doTask();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void doTask() {
            }
        });
    }
}