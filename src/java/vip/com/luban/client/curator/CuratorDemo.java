package com.luban.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @date 2020/8/21 15:48
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class CuratorDemo {

    public static void main(String[] args) throws Exception {
        //重试策略为：每隔1000毫秒重试一次，最多重试3次
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.43.122:2181",new RetryNTimes(3,1000));
        client.start();
        //流式的写法
        client.create().withMode(CreateMode.EPHEMERAL).forPath("/mechine","computer".getBytes());

        String path = "/mechine";
        NodeCache nodeCache = new NodeCache(client,path);
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            //node.start(true) 创建节点的时候就把节点内容放进cache, 所以创建的时候cache中的内容和节点的内容一致，所以不会触发这个监听器
            //node.start(false) 创建节点的时候不会把节点内容放进cache，所以cache中内容为空，和节点的值不一样，所以会触发监听器
            public void nodeChanged() throws Exception {
                System.out.println("123123");
            }
        });

        //Curator也是基于原生客户端的二次开发，我们也可以直接使用原生的客户端只监听一次
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("使用的是原生的wathch");
            }
        });

        System.in.read();
    }
}