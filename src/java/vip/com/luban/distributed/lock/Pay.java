package com.luban.distributed.lock;
/** 
 * @date 2020/8/28 14:37
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Pay {

    public  void pay(){
        System.out.println(Thread.currentThread().getName() + "支付成功");
    }
}