package com.yang.rpc;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.zookeeper.ZooKeeper;

import com.yang.common.GlobalEnv;
import com.yang.common.OwnEnv;

import rpc.service.RpcSendHttpAppHost;

public class RpcClient implements Runnable{
	
	private ZooKeeper zk;

	@Override
	public void run() {
		try {
			zk=GlobalEnv.connectZkServer();
			byte[] info=zk.getData(GlobalEnv.getEngine2path(),null, null);
			String ip=new String(info).split("/")[1];
			int port=Integer.parseInt(new String(info).split("/")[2]);
			
			NettyTransceiver client=new NettyTransceiver(
					new InetSocketAddress(ip, port));
			
			RpcSendHttpAppHost proxy=SpecificRequestor.getClient(RpcSendHttpAppHost.class,client);
			
			while(true){
				//从队列中取出结果map 发生给二级引擎
				proxy.sendHahMap(OwnEnv.getMapQueue().take());
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
