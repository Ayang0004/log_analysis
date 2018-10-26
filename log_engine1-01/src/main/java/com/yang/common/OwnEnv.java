package com.yang.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import rpc.domain.FileSplit;
import rpc.domain.HttpAppHost;

public class OwnEnv {
	
	
	static{
		initParam();
	}
	
	private static BlockingQueue<FileSplit> spiltQueue=new LinkedBlockingQueue<>();
	private static BlockingQueue<Map<CharSequence,HttpAppHost>> mapQueue=new LinkedBlockingQueue<Map<CharSequence,HttpAppHost>>();
	
	private static int rpcport;
	
	private static String znodepath;
	
	public static void initParam(){
		try {
			Properties pro=new Properties();
			InputStream in=OwnEnv.class.getResourceAsStream("/ownenv.properties");
			pro.load(in);
			in.close();
			
			if(pro.containsKey("zebra.rpcport")){
				rpcport=Integer.parseInt(pro.getProperty("zebra.rpcport"));
			}
			if(pro.containsKey("zebra.zk.znodepath")){
				znodepath=pro.getProperty("zebra.zk.znodepath");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getRpcport() {
		return rpcport;
	}

	public static String getZnodepath() {
		return znodepath;
	}

	public static BlockingQueue<FileSplit> getSpiltQueue() {
		return spiltQueue;
	}

	public static BlockingQueue<Map<CharSequence, HttpAppHost>> getMapQueue() {
		return mapQueue;
	}
	
	
	
}
