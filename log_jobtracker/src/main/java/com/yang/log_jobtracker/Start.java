package com.yang.log_jobtracker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yang.file.FileHandle;
import com.yang.file.FileToBlock;
import com.yang.zk.ZkConnectRunner;


public class Start {
	
	public static void main(String[] args) {
		System.out.println("jobtracker启动");
		ExecutorService es=Executors.newCachedThreadPool();
		es.execute(new FileHandle());
		es.execute(new FileToBlock());
		es.execute(new ZkConnectRunner());
	}

}
