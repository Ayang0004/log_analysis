package com.yang.zk;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.yang.common.GlobalEnv;
import com.yang.rpc.RpcClientRunner;


/**
 * 通过zk服务器获取一级引擎的节点数量，方式是获取 /engine1的子节点数量
 * 每有一个节点，就会启动一个rpc客户端线程去发送任务
 * @author zxy
 *
 */
public class ZkConnectRunner implements Runnable{
	
	private ZooKeeper zk;

	@Override
	public void run() {
		try {
			zk=GlobalEnv.connectZkServer();
			//获取子节点
			List<String> childs=zk.getChildren(GlobalEnv.getEngine1path(),null);
			
			for(String childPath:childs){
				//每循环一次，都会启动一个rpc客户端线程
				new Thread(new RpcClientRunner(zk,childPath)).start();
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
