package com.yang.file;

import java.io.File;

import com.yang.common.GlobalEnv;


/**
 * 这是一个线程类，用于定期扫描指定目录下时候有待处理文件
 * 扫描到日志文件之后，加到队列中供后续处理
 * @author zxy
 *
 */
public class FileHandle implements Runnable{

	@Override
	public void run() {
		while(true){
			//获取日志文件的存储目录
			//我放到了D:\\program\\zebra\\data
			File dir=new File(GlobalEnv.getDir());
			//获取文件目录下的所有文件
			File[] files=dir.listFiles();
			
			for(File file:files){
				//根据标识文件(.ctr)找对应日志文件(.csv)
				if(file.getName().endsWith(".ctr")){
					//获取对应的日志文件名
					String csvName=
							file.getName().split(".ctr")[0]+".csv";
					//根据文件名拿到对应的文件
					File csvFile=new File(dir,csvName);
					//将文件放到队列中
					GlobalEnv.getFileQueue().add(csvFile);
					
					//删除当前的表示文件，避免重复处理
					file.delete();
					
				}
			}
			try {
				//设定扫描周期
				Thread.sleep(GlobalEnv.getScannningInterval());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
