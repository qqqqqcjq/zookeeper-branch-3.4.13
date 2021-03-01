package com.luban.distributed.config;

import com.luban.distributed.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @date 2020/8/29 11:09
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class Main {

    public static void main(String[] args) {
        Config config = new Config();
        config.save("timeout", "100");

        for(int i = 0; i < 100; i++){
            System.out.println(config.get("timeout"));

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}