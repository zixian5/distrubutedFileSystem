package fileStorage;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import model.FileStorage;

public class StorageServerTimerTask extends TimerTask {
	private Logger logger=Logger.getLogger(StorageServerTimerTask.class);
	private String name;
	public StorageServerTimerTask(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
	}
	@Override
	public void run() {
		try {
			byte[] bs=name.getBytes();
			DatagramPacket datagramPacket=new DatagramPacket(bs, bs.length,InetAddress.getByName("localhost"),4321);
		    DatagramSocket socket=new DatagramSocket();
		    socket.send(datagramPacket);
		    logger.info(name+"存储服务器向文件服务器发送一个心跳包");
		    socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
