package com.luban.distributed.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @date 2020/8/28 14:38
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Main {

    public static void main(String[] args) {
        Thread thread1 = new Thread(new UserThread() ,"user1");
        Thread thread2 = new Thread(new UserThread() ,"user2");

        thread1.start();
        thread2.start();
    }

    //这个锁只能在一个jvm实例上用，我们需要在分布式系统中也可以生效的锁：分布式锁
    //static Lock lock = new ReentrantLock();

    static  Lock lock = new Zklock();

    static  class UserThread implements  Runnable{
        @Override
        public void run() {
            new Order().createOrder();
            lock.lock();
            boolean result = new Stock().reduceStock();
            lock.unlock();
            if(result){
                System.out.println(Thread.currentThread().getName() + "减库存成功");
                new Pay().pay();
            } else {
                System.out.println(Thread.currentThread().getName() + "减库存失败");
            }
        }
    }
}