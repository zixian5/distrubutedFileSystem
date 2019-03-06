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
	 * �������Դ洢���������ļ������͸��ͻ���
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean sendFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("�ļ��������յ�����ָ��");
		String uuid = dis.readUTF();
		logger.info("�ļ��������յ��ļ�uuid��" + uuid);
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
			logger.error("���ҵ��ļ�������");
			dos.writeBoolean(false);
			dis.close();
			dos.flush();
			dos.close();
			return false;
		}
		logger.info("���ҵ��ļ�����");
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
			logger.error("û�п��õĴ洢������");
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
			logger.info("֪ͨ�ͻ����ļ���Ϣ");
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
			logger.info("֪ͨ�ͻ����ļ���Ϣ");
			dis.close();
			dos.flush();
			dos.close();
		}
		return true;
	}
	/*
	 * �������Կͻ��˵��ϴ��ļ�����
	 */
	private boolean recieveFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("�ļ��������յ��ϴ�ָ��");
		long size = dis.readLong();
		logger.info("�ļ��������յ��ļ���С:" + size);
		
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
			dos.writeUTF("û�п��õ��㹻����ļ��洢��");
			dos.flush();
			return false;
		}
		dos.writeBoolean(true);
		dos.flush();
		String fileName = dis.readUTF();
		logger.info("�ļ����������յ��ļ����ƣ�" + fileName);
		logger.info("�ļ�������ȷ�Ͽ����ϴ�");
		List<FileStorage> max2=StorageServerUtil.getMaxDoubleFileStorage();
		dos.writeUTF(max2.get(0).getIP());
		dos.writeInt(max2.get(0).getPort());
		dos.writeUTF(max2.get(1).getIP());
		dos.writeInt(max2.get(1).getPort());
		logger.info("֪ͨ�ͻ����ļ��������ͱ��ݷ������ĵ�ַ�Ͷ˿ں�");
		String uuid=StorageServerUtil.getUUID();
		dos.writeUTF(uuid);
		List<FileNode> fileNodes=FileServer.fileNodes;
		FileNode fileNode=new FileNode(uuid, fileName, size,max2.get(0).getIP(), max2.get(0).getPort(), max2.get(1).getIP(),max2.get(1).getPort());
		fileNodes.add(fileNode);
		StorageServerUtil.writeObject(fileNodes);
		logger.info("�������Ѿ����ļ���Ϣ����");
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
	 * ɾ���ļ�
	 * 
	 * @param socket
	 * @throws IOException
	 */
	private boolean deleteFile(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub
		logger.info("�ļ��������յ�ɾ��ָ��");
		String uuid;
		try {
			uuid = dis.readUTF();
			logger.info("�ļ��������յ��ļ�uuid:" + uuid);
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
			dos.writeBoolean(true);//���ڸ��ļ��ڵ����ɾ��
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
				dos.writeBoolean(true);//�洢�ڵ�򿪿�������ɾ��
			}else{
				logger.error("�洢������û�д򿪣�����ɾ��");
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
	 * ����淵���ļ�����Ϣ
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
	 * ����淵���ļ��洢�ڵ���Ϣ
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
			case 'u':// �ϴ�ָ��
				if (recieveFile(socket, dis, dos)) {
					logger.info("�ļ��������ϴ��ļ��ɹ�");
				} else {
					logger.info("�ļ��������ϴ��ļ�ʧ��");
				}
				break;
			case 'd':// ����ָ��
				if (sendFile(socket, dis, dos)) {
					logger.info("�ļ������������ļ��ɹ�");
				} else {
					logger.info("�ļ������������ļ�ʧ��");
				}
				break;
			case 'r':// ɾ��ָ��
				if (deleteFile(socket, dis, dos)) {
					logger.info("�ļ�������ɾ���ļ��ɹ�");
				} else {
					logger.info("�ļ�������ɾ���ļ�ʧ��");
				}
				break;
			case 'o'://����ļ�����Ϣ
				if (getFile(socket, dis, dos)) {
					logger.info("�ļ����񷵻��ļ���Ϣ�ɹ�");
				} else {
					logger.info("�ļ������������ļ���Ϣʧ��");
				}
				break;
			case 'z'://����ļ�����Ϣ
				if (getFileStoragy(socket, dis, dos)) {
					logger.info("�ļ����񷵻��ļ��洢�ڵ���Ϣ�ɹ�");
				} else {
					logger.info("�ļ������������ļ��洢�ڵ���Ϣʧ��");
				}
				break;
			case 'q'://ɾ���ϴ�ʧ�ܵ��ļ�����Ϣ
				if (delete(socket, dis, dos)) {
					logger.info("�ļ����񷵻��ļ��洢�ڵ���Ϣ�ɹ�");
				} else {
					logger.info("�ļ������������ļ��洢�ڵ���Ϣʧ��");
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
