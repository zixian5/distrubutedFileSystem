package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;

import org.apache.log4j.Logger;

import model.FileStorage;
import util.StorageServerUtil;

public class UDPProtocol extends Thread{
	private Logger logger = Logger.getLogger(getClass());
	String name="";
	public UDPProtocol(String name)
	{
		this.name=name;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			updateStorageServerInfo(name);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void updateStorageServerInfo(String name) throws ClassNotFoundException, IOException
	{
		FileServer.timerMap.get(name).cancel();
		FileServer.timerMap.put(name, new Timer());
		FileServer.timerMap.get(name).schedule(new FileServerTimerTask(name),
				10 * 1000);
		FileStorage fileStorage=StorageServerUtil.findByName(name);
		fileStorage.setIsAlive(true);
		logger.info("文件服务器收到来自存储服务器 "+name+" 的心跳包");
		StorageServerUtil.addStorageServerToSystem(fileStorage);
	}
}
