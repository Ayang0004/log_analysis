package com.yang.rpc;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import com.yang.common.GlobalEnv;

import rpc.domain.FileSplit;
import rpc.service.RpcFileSplit;

/**
 * 作用:通过zk获取对应节点的ip地址和rpc服务端端口号
 * 建立avro 的rpc客户端，通过相关协议，将任务发送给一级引擎节点
 * @author zxy
 *
 */
public class RpcClientRunner implements Runnable{
	private ZooKeeper zk;
	private String childPath;

	public RpcClientRunner(ZooKeeper zk, String childPath) {
		this.zk=zk;
		this.childPath=childPath;
	}

	@Override
	public void run() {
		try {
			final String path=GlobalEnv.getEngine1path()+"/"+childPath;
			byte[] info=zk.getData(path,null,null);
			String ip=new String(info).split("/")[1];
			int port=Integer.parseInt(new String(info).split("/")[2]);
			
			NettyTransceiver client=new NettyTransceiver(
					new InetSocketAddress(ip, port));
			final RpcFileSplit proxy=
					SpecificRequestor.getClient(RpcFileSplit.class, client);
			
			//从队列中取出一个任务发送给一级引擎
			proxy.sendFileSplit(GlobalEnv.getSplitQueue().take());
			
			for(;;){
				final CountDownLatch cdl=new CountDownLatch(1);
				
				zk.getData(path,new Watcher(){

					@Override
					public void process(WatchedEvent event) {
						try {
							if(event.getType().equals(EventType.NodeDataChanged)){
								byte[] data=zk.getData(path,null,null);
								
								if(new String(data).contains("busy")){
									//不发任务,别忘了线程递减
									cdl.countDown();
								}else{
									FileSplit task=GlobalEnv.getSplitQueue().poll();
									if(task==null){
										//没有任务可发
										cdl.countDown();
									}else{
										//发送任务
										proxy.sendFileSplit(task);
										cdl.countDown();
									}
								}
								
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						
					}
					
				},null);
				
				
				cdl.await();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
