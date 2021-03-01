package com.luban.distributed.lock;
/** 
 * @date 2020/8/28 14:35
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Stock {

    private static Integer COUNT = 1;

    public boolean reduceStock(){
        if(COUNT > 0){
            COUNT--;
            return true;
        }
        return false;
    }
}