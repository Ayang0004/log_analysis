package com.yang.log_engine2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yang.rpc.RpcServer;
import com.yang.zk.ZkConnectRunner;


public class Start {
	
	public static void main(String[] args) {
		System.out.println("二级引擎启动");
		
		ExecutorService es=Executors.newCachedThreadPool();
		es.execute(new ZkConnectRunner());
		es.execute(new RpcServer());
	}

}
