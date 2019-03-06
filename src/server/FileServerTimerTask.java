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
			logger.info("�ļ�����������10��û���յ����Դ洢������ "+ss.getName()+" ��������");
			logger.info("�ļ����������洢������ "+ss.getName()+" ����Ϊdead");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}