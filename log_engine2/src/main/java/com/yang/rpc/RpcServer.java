package com.yang.rpc;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import rpc.service.RpcSendHttpAppHost;

/**
 * 二级引擎的rpc服务端，用于接收一级引擎发来的结果map
 * @author zxy
 *
 */
public class RpcServer implements Runnable{

	@Override
	public void run() {
		try {
			NettyServer server=new NettyServer(
					new SpecificResponder(RpcSendHttpAppHost.class, new RpcHttpAppHostImpl()),
					new InetSocketAddress(8888));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	

}
