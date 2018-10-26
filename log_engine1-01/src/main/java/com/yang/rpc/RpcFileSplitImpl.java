package com.yang.rpc;

import org.apache.avro.AvroRemoteException;

import com.yang.common.OwnEnv;

import rpc.domain.FileSplit;
import rpc.service.RpcFileSplit;

public class RpcFileSplitImpl implements RpcFileSplit{

	@Override
	public Void sendFileSplit(FileSplit fileSplit) throws AvroRemoteException {
		//将任务切片放到一级引擎自己的队列中
		OwnEnv.getSpiltQueue().add(fileSplit);
		return null;
	}

}
