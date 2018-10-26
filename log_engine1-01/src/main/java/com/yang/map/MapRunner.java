package com.yang.map;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.yang.common.OwnEnv;
import com.yang.zk.ZkConnectRunner;

import rpc.domain.FileSplit;
import rpc.domain.HttpAppHost;

/**
 * 是一个线程类，用于从队列中取出任务切片
 * 根据任务切片的描述信息处理对应的日志数据
 * 
 * 一行一行的处理，然后按 |切割
 * 然后根据业务要求封装字段并做数据的合并
 * @author zxy
 *
 */
public class MapRunner implements Runnable{

	@Override
	public void run() {
		try {
			while(true){
				//用于对象数据的合并
				Map<CharSequence,HttpAppHost> map=new HashMap<>();
				
				FileSplit split=OwnEnv.getSpiltQueue().take();
				//进入繁忙状态
				ZkConnectRunner.setBusy();
				
				File file=new File(split.getPath().toString());
				//处理的起始位置
				long start=split.getStart();
				//处理的终止位置
				long end=start+split.getLength();
			
				
				//获取文件通道
				FileChannel fc=new FileInputStream(file).getChannel();
				
				//--做start的位置追溯
				if(start==0){
					//不需要处理
				}else{
					long headPosition=start;
					while(true){
						fc.position(headPosition);
						ByteBuffer data=ByteBuffer.allocate(1);
						fc.read(data);
						if(new String(data.array()).equals("\n")){
							start=headPosition+1;
							break;
						}else{
							headPosition--;
						}
					}
				}
				
				//--end的位置追溯
				if(end==file.length()){
					//不需要处理
				}else{
					long tailPostion=end;
					while(true){
						fc.position(tailPostion);
						ByteBuffer data=ByteBuffer.allocate(1);
						fc.read(data);
						if(new String(data.array()).equals("\n")){
							end=tailPostion;
							break;
						}else{
							tailPostion--;
						}
					}
				}
				
				
				fc.position(start);
				ByteBuffer buffer=ByteBuffer.allocate((int) (end-start));
				fc.read(buffer);
				
				//--一行一行处理
				BufferedReader br=new BufferedReader(
						new InputStreamReader(
					new ByteArrayInputStream(buffer.array())));
				
				String line=null;
				while((line=br.readLine())!=null){
					String[] data=line.split("\\|");
					
					HttpAppHost hah=new HttpAppHost();
					//获取日志文件的日志，可以通过文件名来获取
					String reportTime=file.getName().split("_")[1];
					
					hah.setReportTime(reportTime);
					//上网小区的id
					hah.setCellid(data[16]);
					//应用类
					hah.setAppType(Integer.parseInt(data[22]));
					//应用子类
					hah.setAppSubtype(Integer.parseInt(data[23]));
					//用户ip
					hah.setUserIP(data[26]);
					//用户port
					hah.setUserPort(Integer.parseInt(data[28]));
					//访问的服务ip
					hah.setAppServerIP(data[30]);
					//访问的服务port
					hah.setAppServerPort(Integer.parseInt(data[32]));
					//域名
					hah.setHost(data[58]);
					
					//appTypeCode是请求状态码，我们只关注是否为103,
					//如果是103，表示是Http请求
					int appTypeCode=Integer.parseInt(data[18]);
					String transStatus=data[54];
				

					//业务逻辑处理
					if(hah.getCellid()==null||hah.getCellid().equals("")){
						hah.setCellid("000000000");
					}
					if(appTypeCode==103){
						hah.setAttempts(1);
					}
					if(appTypeCode==103&&"10,11,12,13,14,15,32,33,34,35,36,37,38,48,49,50,51,52,53,54,55,199,200,201,202,203,204,205,206,302,304,306".contains(transStatus)){
						hah.setAccepts(1);
					}else{
						hah.setAccepts(0);
					}
					if(appTypeCode == 103){
						hah.setTrafficUL(Long.parseLong(data[33]));
					}
					if(appTypeCode == 103){
						hah.setTrafficDL(Long.parseLong(data[34]));
					}
					if(appTypeCode == 103){
						hah.setRetranUL(Long.parseLong(data[39]));
					}
					if(appTypeCode == 103){
						hah.setRetranDL(Long.parseLong(data[40]));
					}
					if(appTypeCode==103){
						hah.setTransDelay(Long.parseLong(data[20]) - Long.parseLong(data[19]));
					}
					
					CharSequence key=hah.getReportTime() + "|" + hah.getAppType() + "|" + hah.getAppSubtype() + "|" + hah.getUserIP() + "|" + hah.getUserPort() + "|" + hah.getAppServerIP() + "|" + hah.getAppServerPort() +"|" + hah.getHost() + "|" + hah.getCellid();
					
					//tom 2323
					//①tom 2323
					//tom 3443
					//②tom 2323+3443
					//将 tom 2323+3443放回到map里
					if(map.containsKey(key)){
						HttpAppHost mapHah=map.get(key);
						mapHah.setAccepts(mapHah.getAccepts()+hah.getAccepts());
						mapHah.setAttempts(mapHah.getAttempts()+hah.getAttempts());
						mapHah.setTrafficUL(mapHah.getTrafficUL()+hah.getTrafficUL());
						mapHah.setTrafficDL(mapHah.getTrafficDL()+hah.getTrafficDL());
						mapHah.setRetranUL(mapHah.getRetranUL()+hah.getRetranUL());
						mapHah.setRetranDL(mapHah.getRetranDL()+hah.getRetranDL());
						mapHah.setTransDelay(mapHah.getTransDelay()+hah.getTransDelay());
						
						map.put(key,mapHah);
						

						
					}else{
						map.put(key,hah);
					}

				}
				//进入空闲状态
				ZkConnectRunner.setFree();
				//将结果map存到一级引擎自己的map队列里
				OwnEnv.getMapQueue().add(map);
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
