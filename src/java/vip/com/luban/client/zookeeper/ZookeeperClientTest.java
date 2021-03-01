package com.luban.client.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @date 2020/8/21 14:12
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
//Zookeeper原生客户端设置的监听器只可以监听一次，后面的事件就不监听了
public class ZookeeperClientTest {

    public static void main(String[] args) {
        try{
            ZooKeeper client = new ZooKeeper("localhost:2181",50000,new Watcher(){
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("连接的时候" + event);
                }
            });

            //stat用来保存返回的节点状态信息
            Stat stat = new Stat();
            //value用来保存节点的内容
            byte[] value = client.getData("/paper", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(Event.EventType.NodeDataChanged.equals(event.getType())){
                        System.out.println("/paper 节点内容发生了改变");
                    }
                }
            },stat);
            String s = new String(value);
            System.out.println(s);

            //设置一个回调函数， 其实监听器也是回调函数
            client.getData("/paper", false, new AsyncCallback.DataCallback() {
                @Override
                public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                    System.out.println("123123123");
                }
            }, stat);

            System.in.read();
        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }
}