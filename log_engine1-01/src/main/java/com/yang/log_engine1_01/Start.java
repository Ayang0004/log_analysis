package com.yang.log_engine1_01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yang.map.MapRunner;
import com.yang.rpc.RpcClient;
import com.yang.rpc.RpcServer;
import com.yang.zk.ZkConnectRunner;


public class Start {

	public static void main(String[] args) {
		System.out.println("一级引擎01节点启动");
		ExecutorService es=Executors.newCachedThreadPool();
		es.execute(new ZkConnectRunner());
		es.execute(new RpcServer());
		es.execute(new MapRunner());
		es.execute(new RpcClient());
	}
}
