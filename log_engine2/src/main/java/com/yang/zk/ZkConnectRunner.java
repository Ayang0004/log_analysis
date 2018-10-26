package com.yang.zk;

import java.net.InetAddress;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.yang.common.GlobalEnv;


/**
 * 用于二级引擎向zk注册自身的ip和rpc端口号
 * @author zxy
 *
 */
public class ZkConnectRunner implements Runnable{
	
	private ZooKeeper zk;

	@Override
	public void run() {
		try {
			zk=GlobalEnv.connectZkServer();
			//--/engine2
			String path=GlobalEnv.getEngine2path();
			String data=InetAddress.getLocalHost()+"/8888";
			
			zk.create(path, data.getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
