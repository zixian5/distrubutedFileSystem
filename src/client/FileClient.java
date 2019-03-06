package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import exception.FileNotExist;
import exception.IsNotFileException;
import exception.StorageServerInvalibException;
import exception.WrongArgumentException;
import server.NwServer;
import test.ZipUtils;

public class FileClient {
	private Logger logger = Logger.getLogger(FileClient.class);

	private Socket socket = null;

	private DataInputStream dis = null;
	private DataOutputStream dos = null;

	public FileClient() throws UnknownHostException, IOException {
		socket = new Socket("localhost", 4321);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
	}
	/**
	 * 下载
	 * 
	 * @param fileUUID
	 * @return
	 * @throws IOException
	 */
	private boolean download(String fileUUID) throws IOException {
		// TODO Auto-generated method stub
		dos.writeChar('d');
		dos.writeUTF(fileUUID);
		dos.flush();
		logger.info("客户端发送文件uuid给文件服务器");
		Boolean result=dis.readBoolean();
		if(result==false)
		{
			logger.error("查找的uuid不存在");
			return false;
		}
		int FileStoragyNum=dis.readInt();
		//读取可用的文件服务器的数目
		if(FileStoragyNum==0)
		{
			logger.error("没有可用的文件服务器");
			return false;
		}
		if(FileStoragyNum==2){
		String fileName = dis.readUTF();
		logger.info("客户端接收到文件服务器发送的文件名称:" + fileName);
		long size = dis.readLong();
		logger.info("客户端接收到文件服务器发送的文件大小:" + size);
		File file = new File("E:\\work\\buffer\\"+fileName);
		if (file.exists()) {
			String newName = file.getName().substring(0,
					file.getName().indexOf('.'))
					+ "(1)"
					+ file.getName().substring(file.getName().indexOf('.'));
			System.out.println("此文件已存在，自动重命名为" + newName);
	     	file=new File("E:\\work\\buffer\\"+newName);
		}
		System.out.println(file.getAbsolutePath());
		String mainIP=dis.readUTF();
		int mainPort=dis.readInt();
		String copyIP=dis.readUTF();
		int copyPort=dis.readInt();
		dis.close();
		dos.close();
		Socket socket2=new Socket(mainIP, mainPort);
		DataOutputStream dos=new DataOutputStream(socket2.getOutputStream());
		DataInputStream  dis=new DataInputStream(socket2.getInputStream());
		FileOutputStream fos = new FileOutputStream(file);
		dos.writeChar('d');
		dos.writeUTF(fileUUID);
		dos.flush();
		logger.info("客户端开始接收文件服务器发送的文件");
		logger.info("客户端开始接收文件服务器发送的文件");
		// 文件传输
		long passedLen = 0;// 当前一共传输大小
		int bufferSize = 8192;// 缓冲区大小
		byte[] buf = new byte[bufferSize];// 缓冲区
		try {
			while (true) {
				int read = 0;
				if (dis != null) {
					read = dis.read(buf);
				}
				if (read == -1) {
					break;
				}
				logger.info("已经传输:"+passedLen/(size*1.0)*100+"%");
				passedLen += read;
				fos.write(buf, 0, read);
			}
		} catch (SocketException e) {
			logger.error("客户端与服务器的连接中断，下载终止，从备份服务器下载");
			// 关闭资源
			dos.close();
			dis.close();
			boolean b=file.delete();
			if(b)
			{
				logger.info("删除之前的文件成功");
			}
			 socket2=new Socket(copyIP, copyPort);
			 dos=new DataOutputStream(socket2.getOutputStream());
			  dis=new DataInputStream(socket2.getInputStream());
			 fos = new FileOutputStream(file);
			dos.writeChar('d');
			dos.writeUTF(fileUUID);
			dos.flush();
			logger.info("客户端开始接收文件服务器发送的文件");
			logger.info("客户端开始接收文件服务器发送的文件");
			// 文件传输
			 passedLen = 0;// 当前一共传输大小
			 bufferSize = 8192;// 缓冲区大小
			 buf = new byte[bufferSize];// 缓冲区
			try {
				while (true) {
					int read = 0;
					if (dis != null) {
						read = dis.read(buf);
					}
					if (read == -1) {
						break;
					}
					logger.info("已经传输:"+passedLen/(size*1.0)*100+"%");
					passedLen += read;
					fos.write(buf, 0, read);
				}
			} catch (SocketException e1) {
				logger.error("客户端与服务器的连接中断，下载终止");
			// 关闭资源			
				fos.close();
				dos.close();
				dis.close();
				socket2.close();
				return false;
			}
			socket2.close();
			fos.close();
			dos.close();
			dis.close();
			if (passedLen == size) {
				logger.info("客户端接收文件成功");
				return true;
			} else {
				logger.info("客户端接收文件失败");
				return false;
			}
		}
		fos.close();
		dos.close();
		dis.close();
		logger.info("下载完成开始解压");
		ZipUtils.unzip(file.getAbsolutePath(), "e:\\work\\client\\"+file.getName());
		logger.info("解压完成");
		if (passedLen == size) {
			logger.info("客户端接收文件成功");
			return true;
		} else {
			logger.info("客户端接收文件失败");
			return false;
		}
		}
		if(FileStoragyNum==1)
		{
			String fileName = dis.readUTF();
			logger.info("客户端接收到文件服务器发送的文件名称:" + fileName);
			long size = dis.readLong();
			logger.info("客户端接收到文件服务器发送的文件大小:" + size);
			File file = new File("E:\\work\\buffer\\"+fileName);
			if (file.exists()) {
				String newName = file.getName().substring(0,
						file.getName().indexOf('.'))
						+ "(1)"
						+ file.getName().substring(file.getName().indexOf('.'));
				System.out.println("此文件已存在，自动重命名为" + newName);
				file.renameTo(new File("E:\\work\\buffer\\"+newName));				
			}
			String mainIP=dis.readUTF();
			int mainPort=dis.readInt();
			dis.close();
			dos.close();
			Socket socket2=new Socket(mainIP, mainPort); 
			DataOutputStream dos=new DataOutputStream(socket2.getOutputStream());
			DataInputStream  dis=new DataInputStream(socket2.getInputStream());
			FileOutputStream fos = new FileOutputStream(file);
			dos.writeChar('d');
			dos.writeUTF(fileUUID);
			dos.flush();
			logger.info("客户端开始接收文件服务器发送的文件");
			logger.info("客户端开始接收文件服务器发送的文件");
			// 文件传输
			long passedLen = 0;// 当前一共传输大小
			int bufferSize = 8192;// 缓冲区大小
			byte[] buf = new byte[bufferSize];// 缓冲区
			try {
				while (true) {
					int read = 0;
					if (dis != null) {
						read = dis.read(buf);
					}
					if (read == -1) {
						break;
					}
					passedLen += read;
					fos.write(buf, 0, read);
				}
			} catch (SocketException e1) {
				logger.error("客户端与服务器的连接中断，下载终止");
			// 关闭资源			
				fos.close();
				dos.close();
				dis.close();
				socket2.close();
				return false;
			}
			socket2.close();
			fos.close();
			dos.close();
			dis.close();
			logger.info("下载完成开始解压");
			ZipUtils.unzip(file.getAbsolutePath(), "e:\\work\\client\\"+file.getName());
			logger.info("解压完成");
			if (passedLen == size) {
				logger.info("客户端接收文件成功");
				return true;
			} else {
				logger.info("客户端接收文件失败");
				return false;
			}
		}
		return true;
	}
	/**
	 * 根据uuid删除文件
	 * 
	 * @param fileUUID
	 * @return
	 * @throws IOException
	 */
	public boolean remove(String fileUUID) throws IOException {
		dos.writeChar('r');
		dos.flush();
		dos.writeUTF(fileUUID);
		dos.flush();
		logger.info("客户端发送文件uuid给文件服务器删除");
		Boolean result0=dis.readBoolean();
		if(result0==false)
		{
			logger.error("没有对应的文件，删除失败");
			return false;
		}
		Boolean result1=dis.readBoolean();
		if(result1==false)
		{
			logger.error("存储服务器关闭无法正常删除");
			return false;
		}
		logger.info("存储服务器正常运行可以删除");
		String mainIp=dis.readUTF();
		Integer mainPort=dis.readInt();//获取主节点的地址和IP地址
		String copyIp=dis.readUTF();
		Integer copyPort=dis.readInt();//获取备份节点的地址和IP地址
		dos.close();
		dis.close();
		socket.close();
		try {
			Socket socket=new Socket(mainIp, mainPort);
			DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			dos.writeChar('r');
			dos.writeUTF(fileUUID);
			boolean result=dis.readBoolean();
			if(result==false)
			{
				return result;
			}
			dos.writeUTF(copyIp);
			dos.writeInt(copyPort);
			dos.close();
			dis.close();
		} catch (SocketException e) {
			// TODO: handle exception
			logger.error("删除节点出错");
		}
		return false;
	}
	/**
	 * 上传
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public boolean upload(String filePath) throws IOException {
		dos.writeChar('u');// 上传文件指令
		dos.flush();
		logger.info("客户端 发送上传指令给文件服务器");
		File file0 = new File(filePath);
		if (!file0.exists()) {
			logger.error("文件不存在");
			throw new FileNotExist("当前文件夹下不存在此文件");
		} else if (!file0.isFile()) {
			logger.info(filePath + "不是一个文件");
			throw new IsNotFileException(filePath + "不是一个文件");
		}
		logger.info("开始加密压缩");
		File file=new File(file0.getAbsolutePath()+"0");
		file.createNewFile();
		ZipUtils.zip(file0.getAbsolutePath(), file0.getName()+"0");
		logger.info("加密压缩完成");
		dos.writeLong(file.length());// 将文件大小告诉文件服务器以确定是否可以上传
		dos.flush();
		logger.info("客户端发送文件大小给文件服务器");
		boolean result = dis.readBoolean();// 文件服务器返回信息告诉客户端是否可上传
		if (!result) {
			String erroInfo = dis.readUTF();// 读出错误信息
			logger.error(erroInfo);
			throw new StorageServerInvalibException("没有可用的存储服务器");
		}
		logger.info("服务器返回确定信息可以上传");
		dos.writeUTF(file0.getName());// 将文件名字发送给服务器
		String mainIp=dis.readUTF();
		Integer mainPort=dis.readInt();//获取主节点的地址和IP地址
		String copyIp=dis.readUTF();
		Integer copyPort=dis.readInt();//获取备份节点的地址和IP地址
		final String filename=dis.readUTF();//获取生成的文件名字
		logger.info("服务器生成的文件名字为"+filename);
		dos.flush();
		dis.close();
		dos.close();
		socket.close();
		logger.info("客户端发送文件名称给文件服务器");
		logger.info("客户端开始上传文件给文件服务器");
		// 文件传输
		Socket socket1=new Socket(mainIp, mainPort);
		//和文件服务器建立链接。
		DataInputStream dis0=new DataInputStream(socket1.getInputStream());
		DataOutputStream dos0=new DataOutputStream(socket1.getOutputStream());
		dos0.writeChar('u');
		dos0.writeLong(file.length());
		dos0.writeUTF(filename);
		logger.info("文件服务器获得文件名字和大小");
		dos0.writeUTF(copyIp);
		dos0.writeInt(copyPort);
		logger.info("文件服务器获得备份节点信息");
		int bufferSize = 4096;// 缓冲区大小
		byte[] buf = new byte[bufferSize];// 缓冲区
		long passedlen = 0; // 已传输大小
		int len = 0; // 每次用read读取返回的值，代表本次上传的字节数
		long size = file.length();// 文件大小
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
			try {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Socket socket2=new Socket("localhost", 4321);
							DataOutputStream dataOutputStream=new DataOutputStream(socket2.getOutputStream());
							dataOutputStream.writeChar('q');
							dataOutputStream.writeUTF(filename);
							dataOutputStream.flush();
							dataOutputStream.close();
							socket2.close();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				Thread.sleep(10*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Socket socket=new Socket("localhost", 4321);
			DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			dos.writeChar('u');// 上传文件指令
			logger.error("服务器与客户端之间的连接断开，尝试重传送文件");
			dos.writeLong(file.length());// 将文件大小告诉文件服务器以确定是否可以上传
			dos.flush();
			logger.info("客户端发送文件大小给文件服务器");
			result = dis.readBoolean();// 文件服务器返回信息告诉客户端是否可上传
			if (!result) {
				String erroInfo = dis.readUTF();// 读出错误信息
				logger.error(erroInfo);
				throw new StorageServerInvalibException("没有可用的存储服务器");
			}
			logger.info("服务器返回确定信息可以上传");
			dos.writeUTF(file.getName());// 将文件名字发送给服务器
			 mainIp=dis.readUTF();
			 mainPort=dis.readInt();//获取主节点的地址和IP地址
			 System.out.println(mainPort);
			 copyIp=dis.readUTF();
			 copyPort=dis.readInt();//获取备份节点的地址和IP地址
			String filename0=dis.readUTF();//获取生成的文件名字
			logger.info("服务器生成的文件名字为"+filename0);
			dos.flush();
			dis.close();
			dos.close();
			socket.close();
			logger.info("客户端发送文件名称给文件服务器");
			logger.info("客户端开始上传文件给文件服务器");
			// 文件传输
			dos.flush();
			 socket1=new Socket(mainIp, mainPort);
			  dis0=new DataInputStream(socket1.getInputStream());
			  dos0=new DataOutputStream(socket1.getOutputStream());
			  dos0.writeChar('u');
			dos0.writeLong(size);
			dos0.writeUTF(filename0);
			logger.info("文件服务器获得文件名字和大小");
			dos0.writeUTF(copyIp);
			dos0.writeInt(copyPort);
			logger.info("文件服务器获得备份节点信息");
			 bufferSize = 4096;// 缓冲区大小
			 buf = new byte[bufferSize];// 缓冲区
			 passedlen = 0; // 已传输大小
			 len = 0; // 每次用read读取返回的值，代表本次上传的字节数
			 size = file.length();// 文件大小
			 fis = new FileInputStream(file);// 打开要上传的文件流
			 bis = new BufferedInputStream(fis);
			final long sector0 = size / 100;
			sectorLen = 0;
			try {
				int percentage = 0;// 已传输百分比
				while (passedlen < size) {
					len = bis.read(buf);
					passedlen += len;
					sectorLen += len;
					if (sectorLen >= sector0) {
						sectorLen = 0;
						percentage++;
						logger.info("已传输%" + percentage);
					}
					dos0.write(buf, 0, len);
					dos0.flush();
				}
				logger.info("已传输%" + 100);
			}catch(IOException exception){
				exception.printStackTrace();
				logger.error("再次出现错误，停止传送");
			}
			fis.close();
			dis0.close();
			dos0.close();
			socket1.close();
			return false;
		} finally {
			// 关闭资源
			fis.close();
			dis0.close();
			dos0.close();
			socket1.close();
		}
		if (passedlen == size) {
			logger.info("客户端文件上传完成");
			return true;
		} else {
			logger.error("客户端文件上传失败");
			return false;
		}
	}
public static void main(String[] args) throws UnknownHostException, IOException {
	Logger logger = Logger.getLogger(FileClient.class);
	String[] a={"download","150051320"};
	if (a.length < 2) {
		throw new WrongArgumentException("参数数量错误");
	}
	FileClient client = new FileClient();
	if (a[0].trim().equals("upload")) {
		if (client.upload(a[1])) {
			logger.info("文件上传成功");
		} else {
			logger.info("文件上传失败");
		}
	} else if (a[0].trim().equals("download")) {
		client.download(a[1]);
	} else if (a[0].trim().equals("remove")) {
		client.remove(a[1]);
	}  else {
		throw new WrongArgumentException("参数错误");
	}
}
}