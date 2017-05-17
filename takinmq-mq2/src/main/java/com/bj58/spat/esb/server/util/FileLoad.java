package com.bj58.spat.esb.server.util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esb.server.config.SubjectFactory;

public class FileLoad {

	private static FileLoad fileLoad = new FileLoad();
	private FileInfo fileInfo = null;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadRenameFactory("FileLoad Thread"));;

	public static FileLoad getInstance() {
		return fileLoad;
	}
	
	public void setFileInfo(String path) throws IOException{
		fileInfo = new FileInfo(new File(path));
	}
	
	public void start() {
		scheduler.scheduleWithFixedDelay(new TimerJob(fileInfo), 3, 3, TimeUnit.MINUTES);
	}
}

class TimerJob implements Runnable {

	private static final Log logger = LogFactory.getLog(TimerJob.class);
	
	private static FileInfo fInfo;
	
	public TimerJob(FileInfo fi){
		fInfo = fi;
	}
	
	@Override
	public void run() {
		try{
			if(fInfo != null){
				File f = new File(fInfo.getFilePath());
				if(f != null){					
					if(f.lastModified() != fInfo.getLastModifyTime()){
						//重新加载
						SubjectFactory.getSubjectFactory().load(fInfo.getFilePath());
						fInfo.setLastModifyTime(f.lastModified());
						logger.info("sub_config is reload.");
					}
				}
			}
			
		}catch(Exception e){
			logger.error("FileLoad is error!");
		}
	}

}