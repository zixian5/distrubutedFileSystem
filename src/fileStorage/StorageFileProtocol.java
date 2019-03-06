package fileStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import server.IOStrategy;

public class StorageFileProtocol implements IOStrategy {
	private Logger logger = Logger.getLogger(StorageFileProtocol.class);

	public boolean recieveFile(Socket socket, DataInputStream dis,
			DataOutputStream dos) throws IOException {
		logger.info("存储服务器接收到上传指令");
		long size = dis.readLong();
		logger.info("存储服务器接收到文件大小:" + size);
		String fileName = dis.readUTF();
		logger.info("存储服务器接收到文件名称:" + fileName);
		String copyIP=dis.readUTF();
		int copyPort=dis.readInt();
		logger.info("存储服务器接收到备份服务器IP和端口号为:"+copyIP+"--"+copyPort);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		System.out.println(path);
		System.out.println(path);
		File file = new File(path);
		logger.info("存储服务器存储文件" + fileName + "于" + path);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		long passedLen = 0;// 当前一共传输大小
		int bufferSize = 8192;// 缓冲区大小
		int readLen = 0;// 本次读取大小
		byte[] buf = new byte[bufferSize];// 缓冲区
		while (passedLen < size) {
			if (size - passedLen >= buf.length) {
				readLen = dis.read(buf, 0, buf.length);
			} else {
				readLen = dis.read(buf, 0, (int) (size - passedLen));
			}
			passedLen = passedLen + readLen;
			bos.write(buf, 0, readLen);
		}
		// 关闭资源
		bos.close();
		fos.close();
		dis.close();
		dos.close();
		socket.close();
		if (passedLen == size) {
			logger.info("存储服务器接收文件成功");
		//	return true;
		} else {
			logger.info("存储服务器接收文件失败");
		//	return false;
		}
		Socket socket1=new Socket(copyIP, copyPort);
		DataInputStream dis0=new DataInputStream(socket1.getInputStream());
		DataOutputStream dos0=new DataOutputStream(socket1.getOutputStream());
		dos0.writeChar('c');
		dos0.writeLong(file.length());
		dos0.writeUTF(file.getName());
		logger.info("备份文件服务器获得文件名字和大小");
		 bufferSize = 4096;// 缓冲区大小
		 buf = new byte[bufferSize];// 缓冲区
		long passedlen = 0; // 已传输大小
		int len = 0; // 每次用read读取返回的值，代表本次上传的字节数
		 size = file.length();// 文件大小
		FileInputStream fis = new FileInputStream(file);// 打开要上传的文件流
		BufferedInputStream bis = new BufferedInputStream(fis);
		final long sector = size / 100;
		long sectorLen = 0;
		try {
			int percentage = 0;// 已传输百分比
			while (passedlen < size) {
				len = bis.read(buf);
				passedlen += len;
				sectorLen += len;
				if (sectorLen >= sector) {
					sectorLen = 0;
					percentage++;
					logger.info("已传输%" + percentage);
				}
				dos0.write(buf, 0, len);
				dos0.flush();
			}
			logger.info("已传输%" + 100);
		} catch (SocketException e) {
			logger.error("服务器与备份服务器之间的连接断开，停止传送文件");
			return false;
		} finally {
			// 关闭资源
			fis.close();
			dis0.close();
			dos0.close();
			socket1.close();
		}
		if (passedlen == size) {
			logger.info("向备份服务器文件上传完成");
			return true;
		} else {
			logger.error("向备份服务器文件上传失败");
			return false;
		}
	}
	/**
	 * 发送文件
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean sendFile(Socket socket, DataInputStream dis,
			DataOutputStream dos) throws IOException {
		logger.info("存储服务器收到下载指令");
		String fullName = dis.readUTF();
		logger.info("存储服务器接收到文件全名：" + fullName);
		String path=fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fullName;
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		int bufferSize = 8192;// 缓冲区大小
		byte[] buf = new byte[bufferSize];// 缓冲区
		long passedlen = 0; // 已传输大小
		long len = 0; // 每次用read读取返回的值，代表本次上传的字节数
		long size = file.length();// 文件大小
		logger.info("存储服务器开始发送文件给文件服务器");
		try{
		while ((len = fis.read(buf)) > 0) {
			passedlen += len;
			logger.info("已经传输:"+passedlen/(size*1.0)*100+"%");
			dos.write(buf, 0, (int) len);
		}
		}
		catch(SocketException exception)
		{
			logger.error("服务器与客户端的连接断开");
			file.delete();
		}
		dos.flush();
		// 关闭资源
		fis.close();
		dis.close();
		dos.close();
		socket.close();
		if (passedlen == size) {
			logger.info("存储服务器发送文件成功");
			return true;
		} else {
			logger.error("存储服务器发送文件失败");
			return false;
		}

	}
	/**
	 * 删除文件
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean deletFile(Socket socket, DataInputStream dis,
			DataOutputStream dos) throws IOException {
		logger.info("存储服务器收到删除指令");
		String fileName = dis.readUTF();
		logger.info("存储服务器接收到文件全名:" + fileName);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		File file = new File(path);
		if (file.delete()) {
			dos.writeBoolean(true);
			logger.info("存储服务器删除文件成功");
			dos.flush();
		}else {
			dos.writeBoolean(false);
			return false;
		}
		String copyIP=dis.readUTF();
		int  copyPort=dis.readInt();
		dis.close();
		dos.close();
		Socket socket2=new Socket(copyIP, copyPort);
		DataOutputStream dos2=new DataOutputStream(socket2.getOutputStream());
		dos2.writeChar('a');
		dos2.writeUTF(fileName);
		dos2.flush();
		dos2.close();
		return true;
	}
	/*
	 * 备份服务器接受文件的指令
	 */
	private boolean copyFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("备份存储服务器接收到上传指令");
		long size = dis.readLong();
		logger.info("备份存储服务器接收到文件大小:" + size);
		String fileName = dis.readUTF();
		logger.info("存储服务器接收到文件名称:" + fileName);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		System.out.println(path);
		System.out.println(path);
		File file = new File(path);
		logger.info("存储服务器存储文件" + fileName + "于" + path);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		long passedLen = 0;// 当前一共传输大小
		int bufferSize = 8192;// 缓冲区大小
		int readLen = 0;// 本次读取大小
		byte[] buf = new byte[bufferSize];// 缓冲区
		while (passedLen < size) {
			if (size - passedLen >= buf.length) {
				readLen = dis.read(buf, 0, buf.length);
			} else {
				readLen = dis.read(buf, 0, (int) (size - passedLen));
			}
			passedLen = passedLen + readLen;
			bos.write(buf, 0, readLen);
		}
		// 关闭资源
		bos.close();
		fos.close();
		dis.close();
		dos.close();
		socket.close();
		if (passedLen == size) {
			logger.info("备份存储服务器接收文件成功");
			return true;
		} else {
			logger.info("备份存储服务器接收文件失败");
			return false;
		}
	}
	/*
	 * 备份删除文件
	 * */
	private boolean deletCopyFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("存储服务器收到删除指令");
		String fileName = dis.readUTF();
		logger.info("存储服务器接收到文件全名:" + fileName);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		File file = new File(path);
		if (file.delete()) {
			logger.info("存储服务器删除文件成功");
			dos.flush();
		}else {
			return false;
		}
		dis.close();
		dos.close();
		return true;
	}
	@Override
	public void service(Socket socket) {
		// TODO Auto-generated method stub
		try {
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		char command = dis.readChar();
		switch (command) {
		case 'u':// 上传指令
			recieveFile(socket, dis, dos);
			break;
		case 'd':// 下载指令
			sendFile(socket, dis, dos);
			break;
		case 'r':// 删除指令
			deletFile(socket, dis, dos);
			break;
		case 'c'://备份文件指令
			copyFile(socket,dis,dos);
			break;
		case 'a'://删除备份节点中的文件
			deletCopyFile(socket, dis, dos);
			break;
		default:
			break;
		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
