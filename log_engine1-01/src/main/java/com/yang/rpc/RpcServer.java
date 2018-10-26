package com.yang.rpc;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import com.yang.common.OwnEnv;

import rpc.service.RpcFileSplit;

/**
 * 一级引擎的rpc服务，用于接收jobtracker发来的任务切片
 * 是一个线程类
 * @author zxy
 *
 */
public class RpcServer implements Runnable{

	@Override
	public void run() {
		try {
			NettyServer server=
					new NettyServer(
						new SpecificResponder(RpcFileSplit.class,new RpcFileSplitImpl()),
						new InetSocketAddress(OwnEnv.getRpcport()));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
