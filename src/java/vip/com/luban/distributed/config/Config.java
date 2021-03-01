package com.luban.distributed.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2020/8/29 10:27
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Config {

    //客户端缓存
    private Map<String,String> cache = new HashMap<>();
    private CuratorFramework client;
    private static  final  String CONFIG_PREFIX = "/CONFIG";


    public Config() {
        this.client = CuratorFrameworkFactory.newClient("192.168.43.122:2181", new RetryNTimes(3,1000));
        client.start();
        init();
    }

    //从zk中同步所有配置项
    public void init(){
        try {
            List<String> childNames = client.getChildren().forPath(CONFIG_PREFIX);
            for (String childName : childNames) {
                String value = new String(client.getData().forPath(CONFIG_PREFIX + "/" + childName));
                cache.put(childName,value);
            }

            ////watcher检测zk中节点变化，有变化更新本地缓存cache
            //绑定一个监听器， 监听/CONFIG子节点的增 删 改 操作

            //第三个参数为true : if true, node contents are cached in addition to the stat
            PathChildrenCache watcher = new PathChildrenCache(client, CONFIG_PREFIX, true);
            watcher.getListenable().addListener(new PathChildrenCacheListener() {

                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    String path = event.getData().getPath();
                    String key = path.replace(CONFIG_PREFIX + "/" , "");
                    if(path.startsWith(CONFIG_PREFIX)){
                        if(PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType())
                            || PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(event.getType())){
                            cache.put(key,new String(event.getData().getData()));
                        } else  if(PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())){
                            cache.remove(key);
                        }
                    }


                }
            });

            watcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String name, String value){
        //更新zk
        //更新本地缓存cache
        String configFullName = CONFIG_PREFIX+"/"+name;
        try {
            Stat stat = client.checkExists().forPath(configFullName);

            //当前配置项不存在就创建， 存在的话就更新
            if(stat == null){
                //creatingParentContainersIfNeeded()父节点不存在就创建
                client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(configFullName,value.getBytes());
            } else {
                client.setData().forPath(configFullName,value.getBytes());
            }

            //本地的key直接使用name,  对应的配置在zk中的key是configFullName
            cache.put(name,value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public String get(String name) {
        return cache.get(name);
    }
}