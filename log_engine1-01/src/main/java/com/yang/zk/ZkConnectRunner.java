package com.yang.zk;

import java.net.InetAddress;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.yang.common.GlobalEnv;
import com.yang.common.OwnEnv;


/**
 * 这是一个线程类，用于一级引擎节点启动时向zk服务注册自己的临时节点
 * 此外，节点的数据是本机的ip地址和rpc服务端口号
 * @author zxy
 *
 */
public class ZkConnectRunner implements Runnable{
	
	private static ZooKeeper zk;

	@Override
	public void run() {
		try {
			zk=GlobalEnv.connectZkServer();
			//--路径为：/engine1/node01
			String path=GlobalEnv.getEngine1path()+OwnEnv.getZnodepath();
			//--数据为：ip/port
			String data=InetAddress.getLocalHost()+"/"+OwnEnv.getRpcport();
			zk.create(path,data.getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public static void setBusy() throws Exception{
		String path=GlobalEnv.getEngine1path()+OwnEnv.getZnodepath();
		String data=InetAddress.getLocalHost()+"/"+OwnEnv.getRpcport()+"/busy";
		
		zk.setData(path, data.getBytes(),-1);
	}

	public static void setFree() throws Exception {
		String path=GlobalEnv.getEngine1path()+OwnEnv.getZnodepath();
		String data=InetAddress.getLocalHost()+"/"+OwnEnv.getRpcport()+"/free";
		
		zk.setData(path, data.getBytes(),-1);
		
	}

}
