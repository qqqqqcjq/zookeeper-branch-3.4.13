package com.luban.distributed.lock;

import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @date 2020/8/28 15:23
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Zklock implements Lock {

    //客户端系统可能有多个线程会同时使用Zklock ,所以使用ThreadLocal
    private ThreadLocal<ZooKeeper>  zk = new ThreadLocal<ZooKeeper>();

    private String LOCK_NAME = "/LOCK_ZK";
    private ThreadLocal<String> CURRENT_NAME = new ThreadLocal<String>();

    public void init() throws IOException {
        if(zk.get() == null){
            zk.set( new ZooKeeper("localhost:2181", 500000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println(Thread.currentThread().getName() + "连接的时候" + event);
                }
            }));
        }

        //确保连接已经就绪
        while(true){
            if (ZooKeeper.States.CONNECTING == zk.get().getState()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else break;
        }
    };

    @Override
    public void lock() {
        try {
            init();
            if(zk.get().exists(LOCK_NAME,null) == null) {
                zk.get().create(LOCK_NAME, new byte[0],ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        if(tryLock()){
            System.out.println(Thread.currentThread().getName() + "已经获取到锁了");
        }
    };

    @Override
    public void unlock(){
        try {
            zk.get().delete(CURRENT_NAME.get(), -1);
            System.out.println(Thread.currentThread().getName() + " " + CURRENT_NAME.get() + " 删除");
            CURRENT_NAME.set(null);
            zk.get().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    };

/*
测试的时候发现课堂上周瑜写的这个方式有点问题：
有可能获取锁的线程已经删除节点了，另外的线程还没来得及注册监听器，这样就会导致这个线程一直阻塞
 */
//    @Override
//    public boolean tryLock(){
//        String nodeName = LOCK_NAME+"/zk_";
//        try {
//            CURRENT_NAME.set(zk.get().create(nodeName,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
//
//            //得到父节点下的所有节点名字
//            List<String> list = zk.get().getChildren(LOCK_NAME,false); //没有父节点的名字，只有zk_0001 zk_0002
//            Collections.sort(list);
//            String minNodeName = list.get(0);
//
//            if(CURRENT_NAME.get().equals(LOCK_NAME + "/" + minNodeName)){
//                return true;
//            } else {
//
//                //找到当前节点的前一个节点
//                String currentNodeSimpleName = CURRENT_NAME.get().substring(CURRENT_NAME.get().lastIndexOf("/" ) + 1);
//                Integer currentNodeIndex = list.indexOf(currentNodeSimpleName);
//                String prevNodeName = list.get(currentNodeIndex - 1);
//
//                //注册监听器，监听前一个节点的删除事件
//                CountDownLatch countDownLatch = new CountDownLatch(1);
//                zk.get().exists(LOCK_NAME + "/" + prevNodeName, new Watcher() {
//                    @Override
//                    public void process(WatchedEvent event) {
//                        if(Event.EventType.NodeDeleted.equals(event.getType())){
//                            //前一个节点被删除，CountDownLatch的state减1变为0，唤醒CountDownLatch的阻塞队列中的所有线程
//                            countDownLatch.countDown();
//                            System.out.println(Thread.currentThread().getName() + "唤醒了");
//
//                        }
//                    }
//                });
//
//                //前一个节点还在，当前线程加入CountDownLatch的阻塞队列，然后调用park()阻塞
//                System.out.println(Thread.currentThread().getName() + "阻塞住了");
//                countDownLatch.await();
//                return true;
//            }
//        } catch (KeeperException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    };


    private ThreadLocal<Boolean> notifyFlag = new ThreadLocal<>();
    @Override
    public boolean tryLock(){
        String nodeName = LOCK_NAME+"/zk_";
        try {
            CURRENT_NAME.set(zk.get().create(nodeName,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));

            //得到父节点下的所有节点名字
            List<String> list = zk.get().getChildren(LOCK_NAME,false); //没有父节点的名字，只有zk_0001 zk_0002
            Collections.sort(list);
            String minNodeName = list.get(0);

            if(CURRENT_NAME.get().equals(LOCK_NAME + "/" + minNodeName)){
                return true;
            } else {

                //找到当前节点的前一个节点
                String currentNodeSimpleName = CURRENT_NAME.get().substring(CURRENT_NAME.get().lastIndexOf("/" ) + 1);
                Integer currentNodeIndex = list.indexOf(currentNodeSimpleName);
                String prevNodeName = list.get(currentNodeIndex - 1);


                while(true && zk.get().getChildren(LOCK_NAME,false).contains(prevNodeName)){
                    Thread.sleep(2000);
                }

                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    };

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}