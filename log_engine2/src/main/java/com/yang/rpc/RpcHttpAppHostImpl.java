package com.yang.rpc;

import java.util.Map;

import org.apache.avro.AvroRemoteException;

import com.yang.common.OwnEnv;

import rpc.domain.HttpAppHost;
import rpc.service.RpcSendHttpAppHost;

public class RpcHttpAppHostImpl implements RpcSendHttpAppHost{

	@Override
	public Void sendHttpAppHost(HttpAppHost httpAppHost) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void sendHahMap(Map<CharSequence, HttpAppHost> hahmap) throws AvroRemoteException {
		//将收到的4个map先放到队列中，
		//最后将4个map的结果合并到一个map中
		System.out.println("二级引擎收到一级引擎发来的数据:"+hahmap.size());
		OwnEnv.getMapQueue().add(hahmap);
		//更严谨的做法是JobTracker将任务数量注册到zk上，然后二级引擎获取这个数量
		if(OwnEnv.getMapQueue().size()==4){
			new Thread(new ReduceRuning()).start();
		}
		
		return null;
	}

}
