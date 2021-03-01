package com.luban.distributed.lock;
/** 
 * @date 2020/8/28 14:39
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Order {

    public void createOrder(){
        System.out.println(Thread.currentThread().getName() + "创建订单");
    }
}