package com.yang.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.yang.common.Log_DB;
import com.yang.common.OwnEnv;

import rpc.domain.HttpAppHost;

public class ReduceRuning implements Runnable {

	
	private static Map<String,HttpAppHost> map=new HashMap<String,HttpAppHost>();
	@Override
	public void run() {
		while(true){
			Map<CharSequence,HttpAppHost> reducemap=OwnEnv.getMapQueue().poll();
			if(reducemap==null){
				break;
			}else{
				for(Entry<CharSequence, HttpAppHost> entry:reducemap.entrySet()){
					HttpAppHost hah =entry.getValue();
					
					CharSequence key=hah.getReportTime() + "|" + hah.getAppType() + "|" + hah.getAppSubtype() +
							"|" + hah.getUserIP() + "|" + hah.getUserPort() + "|" + hah.getAppServerIP() + "|" +
							hah.getAppServerPort() +"|" + hah.getHost() + "|" + hah.getCellid();
					if(map.containsKey(key)){
						HttpAppHost mapHah=map.get(key);
						mapHah.setAccepts(mapHah.getAccepts()+hah.getAccepts());
						mapHah.setAttempts(mapHah.getAttempts()+hah.getAttempts());
						mapHah.setTrafficUL(mapHah.getTrafficUL()+hah.getTrafficUL());
						mapHah.setTrafficDL(mapHah.getTrafficDL()+hah.getTrafficDL());
						mapHah.setRetranUL(mapHah.getRetranUL()+hah.getRetranUL());
						mapHah.setRetranDL(mapHah.getRetranDL()+hah.getRetranDL());
						mapHah.setTransDelay(mapHah.getTransDelay()+hah.getTransDelay());
					}else{
						map.put(key.toString(), hah);
					}
				}
			}
		}
		System.out.println("reduce合并后的map总大小："+map);
		Log_DB.toDb(map);
	}

}
