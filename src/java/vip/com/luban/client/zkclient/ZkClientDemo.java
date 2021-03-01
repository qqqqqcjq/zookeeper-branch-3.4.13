package com.luban.client.zkclient;

import jdbm.helper.Serialization;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.IOException;
import java.util.List;

/**
 * @date 2020/8/21 15:00
 * @author chengjiaqing
 * @version : 0.1
 */ 
 
  
public class ZkClientDemo {
    public static void main(String[] args) throws IOException {
        ZkClient zk = new ZkClient("192.168.43.122:2181",50000,50000,  new SerializableSerializer());
        //zk.createPersistent("/flower", "1");
        zk.subscribeDataChanges("/flower", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("数据被修改了");
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("数据被删除了");
            }
        });

        System.in.read();
    }
}