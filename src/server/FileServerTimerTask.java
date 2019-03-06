package server;


import java.io.IOException;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import model.FileStorage;
import util.StorageServerUtil;


class FileServerTimerTask extends TimerTask {
	
	private Logger logger = Logger.getLogger(getClass());

	private String storageServerName;

	public FileServerTimerTask(String storageServerName) {
		super();
		this.storageServerName = storageServerName;
	}

	@Override
	public void run() {
		FileStorage ss;
		try {
			ss = StorageServerUtil.findByName(storageServerName);
			ss.setName(storageServerName);
			ss.setIsAlive(false);
			System.out.println(storageServerName);
			FileServer.timerMap.get(storageServerName).cancel();
			StorageServerUtil.addStorageServerToSystem(ss);
			logger.info("文件服务器超过10秒没有收到来自存储服务器 "+ss.getName()+" 的心跳包");
			logger.info("文件服务器将存储服务器 "+ss.getName()+" 设置为dead");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}