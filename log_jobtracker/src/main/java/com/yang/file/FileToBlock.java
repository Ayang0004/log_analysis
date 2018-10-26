package com.yang.file;

import java.io.File;

import com.yang.common.GlobalEnv;

import rpc.domain.FileSplit;

/**
 * 这是一个线程类，从队列中取出待处理的文件，
 * 然后做逻辑切块。
 * 本项目中，切块大小是3MB,最后的块的数量是4块
 * 每一个块的信息使用FileSplit对象来封装的
 * @author zxy
 *
 */
public class FileToBlock implements Runnable{

	@Override
	public void run() {
		try {
			while(true){
				File file=GlobalEnv.getFileQueue().take();
				//获取文件的总大小(10MB)
				long size=file.length();
				
				//计算切块数量(4块)
				long numBlock=size%GlobalEnv.getBlocksize()==0?
						size/GlobalEnv.getBlocksize():
						size/GlobalEnv.getBlocksize()+1;
						
				for(int i=0;i<numBlock;i++){
					FileSplit split=new FileSplit();
					//封装文件路径
					split.setPath(file.getPath());
					split.setStart(i*GlobalEnv.getBlocksize());
					
					if(i==numBlock-1){
						//计算最后一块的length
						split.setLength(size-split.getStart());
					}else{
						split.setLength(GlobalEnv.getBlocksize());
					}
					
					//将切片放到队列中供后续处理
					GlobalEnv.getSplitQueue().add(split);
					
				}
				
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}

}
