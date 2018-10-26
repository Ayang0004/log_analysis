package com.yang.common;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import rpc.domain.FileSplit;


/**
 * 这个类是用定义全局变量，以及加载属性文件和读取属性的
 * @author zxy
 *
 */
public class GlobalEnv {
	//在静态代码里初始化
	static{
		initParam();
	}
	
	//文件目录路径
	private static String dir;
	
	private static BlockingQueue<File> fileQueue=new LinkedBlockingQueue<>();
	
	private static BlockingQueue<FileSplit> splitQueue=new LinkedBlockingQueue<>();
	
	private static long scannningInterval;
	
	private static long blocksize;
	
	private static String zkServerip;
	
	private static int sessiontimeout;
	
	private static String jobtrackerpath;
	
	private static String engine1path;
	
	private static String engine2path;
	
	/*
	 * 这个方法是用于分配zk连接对象的方法
	 */
	public static ZooKeeper connectZkServer(){
		try {
			final CountDownLatch cdl=new CountDownLatch(1);
			
			ZooKeeper zk=new ZooKeeper(zkServerip, sessiontimeout,new Watcher(){

				@Override
				public void process(WatchedEvent event) {
					if(event.getState()==KeeperState.SyncConnected){
						System.out.println("连接zk服务成功！");
						cdl.countDown();
					}
					
				}
				
			});
			
			cdl.await();
			return zk;
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * 这个方法用来加载和属性文件的
	 */
	public static void initParam(){
		try {
		Properties pro=new Properties();
		InputStream in=GlobalEnv.class.getResourceAsStream("/env.properties");
		pro.load(in);
		in.close();
		if(pro.containsKey("zebra.dir")){
			dir=pro.getProperty("zebra.dir");
		}
		if(pro.containsKey("zebra.scanninginterval")){
			scannningInterval=Long.parseLong(pro.getProperty("zebra.scanninginterval","30000"));
		}
		if(pro.containsKey("zebra.blocksize")){
			blocksize=Long.parseLong(pro.getProperty("zebra.blocksize"));
		}
		if(pro.containsKey("zebra.zk.serverip")){
			zkServerip=pro.getProperty("zebra.zk.serverip");
		}
		if(pro.containsKey("zebra.zk.sessiontimeout")){
			sessiontimeout=Integer.parseInt(pro.getProperty("zebra.zk.sessiontimeout"));
		}
		if(pro.containsKey("zebra.zk.jobtrackerpath")){
			jobtrackerpath=pro.getProperty("zebra.zk.jobtrackerpath");
		}
		if(pro.containsKey("zebra.zk.engine1path")){
			engine1path=pro.getProperty("zebra.zk.engine1path");
		}
		if(pro.containsKey("zebra.zk.engine2path")){
			engine2path=pro.getProperty("zebra.zk.engine2path");
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getDir() {
		return dir;
	}

	public static BlockingQueue<File> getFileQueue() {
		return fileQueue;
	}

	public static long getScannningInterval() {
		return scannningInterval;
	}

	public static long getBlocksize() {
		return blocksize;
	}

	public static BlockingQueue<FileSplit> getSplitQueue() {
		return splitQueue;
	}

	public static String getJobtrackerpath() {
		return jobtrackerpath;
	}

	public static String getEngine1path() {
		return engine1path;
	}

	public static String getEngine2path() {
		return engine2path;
	}
	
	
}
