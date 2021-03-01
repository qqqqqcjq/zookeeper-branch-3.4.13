package com.luban.client.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * @date 2020/8/21 15:29
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class ZkClientOperator {
    public static void main(String[] args) {
        ZkClient zk = new ZkClient("192.168.43.122:2181",50000,50000,  new SerializableSerializer());
        zk.writeData("/flower","5");

        zk.delete("/flower");
    }
}