package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import fileStorage.StorageFileProtocol;
import model.FileNode;
import model.FileStorage;
import util.StorageServerUtil;

public class ServerFileProtocol implements IOStrategy{
	private Logger logger = Logger.getLogger(ServerFileProtocol.class);
	/**
	 * 接收来自存储服务器的文件并发送给客户端
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean sendFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("文件服务器收到下载指令");
		String uuid = dis.readUTF();
		logger.info("文件服务器收到文件uuid：" + uuid);
		boolean result=false;
		List<FileNode> fileNodes=FileServer.fileNodes;
		FileNode fileNode0=new FileNode();
		for (FileNode fileNode : fileNodes) {
			if(fileNode.getUuid().equals(uuid))
			{
				fileNode0=fileNode;
				result=true;
				break;
			}
		}
		if(result==false)
		{
			logger.error("查找的文件不存在");
			dos.writeBoolean(false);
			dis.close();
			dos.flush();
			dos.close();
			return false;
		}
		logger.info("查找的文件存在");
		dos.writeBoolean(true);
		Map<Integer, FileStorage> files=new HashMap<>();
		List<FileStorage> fileServers=StorageServerUtil.getAllLiveFileStorage();
		int num=0;
		for (FileStorage fileStorage : fileServers) {
			if((fileStorage.getIP().equals(fileNode0.getMainServerIp()))&&(fileStorage.getPort().equals(fileNode0.getMainServerNode()))){
				num=1;
				files.put(1, fileStorage);
			}
			if((fileStorage.getIP().equals(fileNode0.getCopyServerIp()))&&(fileStorage.getPort().equals(fileNode0.getCopyServerNode()))){
				files.put(2, fileStorage);
			}
		}
		if(files.size()==0)
		{
			logger.error("没有可用的存储服务器");
			dos.writeInt(0);
			dos.flush();
			dos.close();
			dis.close();
			return false;
		}else if(files.size()==2){
			dos.writeInt(2);
			dos.writeUTF(fileNode0.getName());
			dos.writeLong(fileNode0.getSize());
			dos.writeUTF(files.get(1).getIP());
			dos.writeInt(files.get(1).getPort());
			dos.writeUTF(files.get(2).getIP());
			dos.writeInt(files.get(2).getPort());
			logger.info("通知客户端文件信息");
			dis.close();
			dos.flush();
			dos.close();
		}else if(files.size()==1)
		{
			dos.writeInt(1);
			dos.writeUTF(fileNode0.getName());
			dos.writeLong(fileNode0.getSize());
			if(num==1){
				dos.writeUTF(files.get(1).getIP());
				dos.writeInt(files.get(1).getPort());
			}else{
				dos.writeUTF(files.get(2).getIP());
				dos.writeInt(files.get(2).getPort());
			}
			logger.info("通知客户端文件信息");
			dis.close();
			dos.flush();
			dos.close();
		}
		return true;
	}
	/*
	 * 处理来自客户端的上传文件请求
	 */
	private boolean recieveFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("文件服务器收到上传指令");
		long size = dis.readLong();
		logger.info("文件服务器收到文件大小:" + size);
		
		List<FileStorage> fileStorages=new ArrayList<>();
		try{
		fileStorages=StorageServerUtil.getAllLiveFileStorage();
		}catch(Exception exception)
		{
			dos.writeBoolean(false);
			return false;
		}
		int num=0;
		for (FileStorage fileStorage : fileStorages) {
			if(fileStorage.getLeftVolume()>=size)
			{
				num++;
			}
		}
		if(num<2)
		{
			dos.writeBoolean(false);
			dos.flush();
			dos.writeUTF("没有可用的足够大的文件存储器");
			dos.flush();
			return false;
		}
		dos.writeBoolean(true);
		dos.flush();
		String fileName = dis.readUTF();
		logger.info("文件服务器接收到文件名称：" + fileName);
		logger.info("文件服务器确认可以上传");
		List<FileStorage> max2=StorageServerUtil.getMaxDoubleFileStorage();
		dos.writeUTF(max2.get(0).getIP());
		dos.writeInt(max2.get(0).getPort());
		dos.writeUTF(max2.get(1).getIP());
		dos.writeInt(max2.get(1).getPort());
		logger.info("通知客户端文件服务器和备份服务器的地址和端口号");
		String uuid=StorageServerUtil.getUUID();
		dos.writeUTF(uuid);
		List<FileNode> fileNodes=FileServer.fileNodes;
		FileNode fileNode=new FileNode(uuid, fileName, size,max2.get(0).getIP(), max2.get(0).getPort(), max2.get(1).getIP(),max2.get(1).getPort());
		fileNodes.add(fileNode);
		StorageServerUtil.writeObject(fileNodes);
		logger.info("服务器已经将文件信息储存");
		dos.flush();
		dos.close();
		dis.close();
		List<FileStorage> fStorages=StorageServerUtil.getAllLiveFileStorage();
		for (FileStorage fileStorage : fStorages) {
			if((fileStorage.getIP().equals(max2.get(0).getIP()))&&(fileStorage.getPort().equals(max2.get(0).getPort()))){
			    System.out.println("-------------------");
				FileStorage fStorage=fileStorage;
				fileStorage.setLeftVolume(fileStorage.getLeftVolume()-size);
				fileStorage.setFileNum(fileStorage.getFileNum()+1);
				StorageServerUtil.addStorageServerToSystem(fStorage);
			}
			if((fileStorage.getIP().equals(max2.get(1).getIP()))&&(fileStorage.getPort().equals(max2.get(1).getPort()))){
				FileStorage fStorage=fileStorage;
				fileStorage.setLeftVolume(fileStorage.getLeftVolume()-size);
				fileStorage.setFileNum(fileStorage.getFileNum()+1);
				StorageServerUtil.addStorageServerToSystem(fStorage);
			}
		}
		return true;
	}
	/**
	 * 删除文件
	 * 
	 * @param socket
	 * @throws IOException
	 */
	private boolean deleteFile(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub
		logger.info("文件服务器收到删除指令");
		String uuid;
		try {
			uuid = dis.readUTF();
			logger.info("文件服务器收到文件uuid:" + uuid);
			List<FileNode> fileNodes=FileServer.fileNodes;
			FileNode fileNode0=new FileNode();
			int flag=0;
			for (FileNode fileNode : fileNodes) {
				if(fileNode.getUuid().equals(uuid))
				{
					flag=1;
					fileNode0=fileNode;
					break;
				}
			}
			if(flag==0)
			{
				dos.close();
				dos.close();
				dos.writeBoolean(false);
			}
			dos.writeBoolean(true);//存在该文件节点可以删除
			List<FileStorage> storages=StorageServerUtil.getAllLiveFileStorage();
			
			int sum=0;
			System.out.println(fileNode0);
			for (FileStorage fileStorage : storages) {
				if((fileStorage.getIP().equals(fileNode0.getMainServerIp()))&&(fileStorage.getPort().equals(fileNode0.getMainServerNode()))){
					sum++;
				}
				if((fileStorage.getIP().equals(fileNode0.getCopyServerIp()))&&(fileStorage.getPort().equals(fileNode0.getCopyServerNode()))){
					sum++;
				}
			}
			if(sum==2)
			{
				dos.writeBoolean(true);//存储节点打开可以正常删除
			}else{
				logger.error("存储服务器没有打开，不能删除");
				dos.writeBoolean(false);
				dos.close();
				dis.close();
				return false;
			}
			dos.writeUTF(fileNode0.getMainServerIp());
			dos.writeInt(fileNode0.getMainServerNode());
			dos.writeUTF(fileNode0.getCopyServerIp());
			dos.writeInt(fileNode0.getCopyServerNode());
			fileNodes.remove(fileNode0);
			StorageServerUtil.writeObject(fileNodes);
			for (FileStorage fileStorage : storages) {
				if((fileStorage.getIP().equals(fileNode0.getMainServerIp()))&&(fileStorage.getPort().equals(fileNode0.getMainServerNode()))){
					FileStorage fileStorage2=fileStorage;
					fileStorage2.setFileNum(fileStorage2.getFileNum()-1);
					fileStorage2.setLeftVolume(fileStorage2.getLeftVolume()+fileNode0.getSize());
					StorageServerUtil.addStorageServerToSystem(fileStorage2);
				}
				if((fileStorage.getIP().equals(fileNode0.getCopyServerIp()))&&(fileStorage.getPort().equals(fileNode0.getCopyServerNode()))){
					FileStorage fileStorage2=fileStorage;
					fileStorage2.setFileNum(fileStorage2.getFileNum()-1);
					fileStorage2.setLeftVolume(fileStorage2.getLeftVolume()+fileNode0.getSize());
					StorageServerUtil.addStorageServerToSystem(fileStorage2);
				}
			}
			dos.close();
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/*
	 * 向界面返回文件的信息
	 */
	private boolean getFile(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub
		List<FileNode> fileNodes=FileServer.fileNodes;
		try {
			dos.writeUTF(new Gson().toJson(fileNodes));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/*
	 * 向界面返回文件存储节点信息
	 */
	private boolean getFileStoragy(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub
		List<FileStorage> fileStorages=StorageServerUtil.getAllLiveFileStorage();
		try {
			dos.writeUTF(new Gson().toJson(fileStorages));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public void service(Socket socket) {
		DataInputStream dis = null;
		DataOutputStream dos = null;
		char command ='a';
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		    command = dis.readChar();
		    switch (command) {
			case 'u':// 上传指令
				if (recieveFile(socket, dis, dos)) {
					logger.info("文件服务器上传文件成功");
				} else {
					logger.info("文件服务器上传文件失败");
				}
				break;
			case 'd':// 下载指令
				if (sendFile(socket, dis, dos)) {
					logger.info("文件服务器下载文件成功");
				} else {
					logger.info("文件服务器下载文件失败");
				}
				break;
			case 'r':// 删除指令
				if (deleteFile(socket, dis, dos)) {
					logger.info("文件服务器删除文件成功");
				} else {
					logger.info("文件服务器删除文件失败");
				}
				break;
			case 'o'://获得文件的信息
				if (getFile(socket, dis, dos)) {
					logger.info("文件服务返回文件信息成功");
				} else {
					logger.info("文件服务器返回文件信息失败");
				}
				break;
			case 'z'://获得文件的信息
				if (getFileStoragy(socket, dis, dos)) {
					logger.info("文件服务返回文件存储节点信息成功");
				} else {
					logger.info("文件服务器返回文件存储节点信息失败");
				}
				break;
			case 'q'://删除上传失败的文件的信息
				if (delete(socket, dis, dos)) {
					logger.info("文件服务返回文件存储节点信息成功");
				} else {
					logger.info("文件服务器返回文件存储节点信息失败");
				}
				break;
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean delete(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub
		try {
			String uuid=dis.readUTF();
			List<FileNode> fileNodes=FileServer.fileNodes;
			FileNode fileNode0=new FileNode();
			for (FileNode fileNode : fileNodes) {
				if(fileNode.getUuid().equals(uuid))
				{
					fileNode0=fileNode;
				}
			}
			fileNodes.remove(fileNode0);
			FileServer.fileNodes.remove(fileNode0);
			StorageServerUtil.writeObject(fileNodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
