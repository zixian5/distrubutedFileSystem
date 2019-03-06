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
		logger.info("�洢���������յ��ϴ�ָ��");
		long size = dis.readLong();
		logger.info("�洢���������յ��ļ���С:" + size);
		String fileName = dis.readUTF();
		logger.info("�洢���������յ��ļ�����:" + fileName);
		String copyIP=dis.readUTF();
		int copyPort=dis.readInt();
		logger.info("�洢���������յ����ݷ�����IP�Ͷ˿ں�Ϊ:"+copyIP+"--"+copyPort);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		System.out.println(path);
		System.out.println(path);
		File file = new File(path);
		logger.info("�洢�������洢�ļ�" + fileName + "��" + path);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		long passedLen = 0;// ��ǰһ�������С
		int bufferSize = 8192;// ��������С
		int readLen = 0;// ���ζ�ȡ��С
		byte[] buf = new byte[bufferSize];// ������
		while (passedLen < size) {
			if (size - passedLen >= buf.length) {
				readLen = dis.read(buf, 0, buf.length);
			} else {
				readLen = dis.read(buf, 0, (int) (size - passedLen));
			}
			passedLen = passedLen + readLen;
			bos.write(buf, 0, readLen);
		}
		// �ر���Դ
		bos.close();
		fos.close();
		dis.close();
		dos.close();
		socket.close();
		if (passedLen == size) {
			logger.info("�洢�����������ļ��ɹ�");
		//	return true;
		} else {
			logger.info("�洢�����������ļ�ʧ��");
		//	return false;
		}
		Socket socket1=new Socket(copyIP, copyPort);
		DataInputStream dis0=new DataInputStream(socket1.getInputStream());
		DataOutputStream dos0=new DataOutputStream(socket1.getOutputStream());
		dos0.writeChar('c');
		dos0.writeLong(file.length());
		dos0.writeUTF(file.getName());
		logger.info("�����ļ�����������ļ����ֺʹ�С");
		 bufferSize = 4096;// ��������С
		 buf = new byte[bufferSize];// ������
		long passedlen = 0; // �Ѵ����С
		int len = 0; // ÿ����read��ȡ���ص�ֵ���������ϴ����ֽ���
		 size = file.length();// �ļ���С
		FileInputStream fis = new FileInputStream(file);// ��Ҫ�ϴ����ļ���
		BufferedInputStream bis = new BufferedInputStream(fis);
		final long sector = size / 100;
		long sectorLen = 0;
		try {
			int percentage = 0;// �Ѵ���ٷֱ�
			while (passedlen < size) {
				len = bis.read(buf);
				passedlen += len;
				sectorLen += len;
				if (sectorLen >= sector) {
					sectorLen = 0;
					percentage++;
					logger.info("�Ѵ���%" + percentage);
				}
				dos0.write(buf, 0, len);
				dos0.flush();
			}
			logger.info("�Ѵ���%" + 100);
		} catch (SocketException e) {
			logger.error("�������뱸�ݷ�����֮������ӶϿ���ֹͣ�����ļ�");
			return false;
		} finally {
			// �ر���Դ
			fis.close();
			dis0.close();
			dos0.close();
			socket1.close();
		}
		if (passedlen == size) {
			logger.info("�򱸷ݷ������ļ��ϴ����");
			return true;
		} else {
			logger.error("�򱸷ݷ������ļ��ϴ�ʧ��");
			return false;
		}
	}
	/**
	 * �����ļ�
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean sendFile(Socket socket, DataInputStream dis,
			DataOutputStream dos) throws IOException {
		logger.info("�洢�������յ�����ָ��");
		String fullName = dis.readUTF();
		logger.info("�洢���������յ��ļ�ȫ����" + fullName);
		String path=fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fullName;
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		int bufferSize = 8192;// ��������С
		byte[] buf = new byte[bufferSize];// ������
		long passedlen = 0; // �Ѵ����С
		long len = 0; // ÿ����read��ȡ���ص�ֵ���������ϴ����ֽ���
		long size = file.length();// �ļ���С
		logger.info("�洢��������ʼ�����ļ����ļ�������");
		try{
		while ((len = fis.read(buf)) > 0) {
			passedlen += len;
			logger.info("�Ѿ�����:"+passedlen/(size*1.0)*100+"%");
			dos.write(buf, 0, (int) len);
		}
		}
		catch(SocketException exception)
		{
			logger.error("��������ͻ��˵����ӶϿ�");
			file.delete();
		}
		dos.flush();
		// �ر���Դ
		fis.close();
		dis.close();
		dos.close();
		socket.close();
		if (passedlen == size) {
			logger.info("�洢�����������ļ��ɹ�");
			return true;
		} else {
			logger.error("�洢�����������ļ�ʧ��");
			return false;
		}

	}
	/**
	 * ɾ���ļ�
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean deletFile(Socket socket, DataInputStream dis,
			DataOutputStream dos) throws IOException {
		logger.info("�洢�������յ�ɾ��ָ��");
		String fileName = dis.readUTF();
		logger.info("�洢���������յ��ļ�ȫ��:" + fileName);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		File file = new File(path);
		if (file.delete()) {
			dos.writeBoolean(true);
			logger.info("�洢������ɾ���ļ��ɹ�");
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
	 * ���ݷ����������ļ���ָ��
	 */
	private boolean copyFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("���ݴ洢���������յ��ϴ�ָ��");
		long size = dis.readLong();
		logger.info("���ݴ洢���������յ��ļ���С:" + size);
		String fileName = dis.readUTF();
		logger.info("�洢���������յ��ļ�����:" + fileName);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		System.out.println(path);
		System.out.println(path);
		File file = new File(path);
		logger.info("�洢�������洢�ļ�" + fileName + "��" + path);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		long passedLen = 0;// ��ǰһ�������С
		int bufferSize = 8192;// ��������С
		int readLen = 0;// ���ζ�ȡ��С
		byte[] buf = new byte[bufferSize];// ������
		while (passedLen < size) {
			if (size - passedLen >= buf.length) {
				readLen = dis.read(buf, 0, buf.length);
			} else {
				readLen = dis.read(buf, 0, (int) (size - passedLen));
			}
			passedLen = passedLen + readLen;
			bos.write(buf, 0, readLen);
		}
		// �ر���Դ
		bos.close();
		fos.close();
		dis.close();
		dos.close();
		socket.close();
		if (passedLen == size) {
			logger.info("���ݴ洢�����������ļ��ɹ�");
			return true;
		} else {
			logger.info("���ݴ洢�����������ļ�ʧ��");
			return false;
		}
	}
	/*
	 * ����ɾ���ļ�
	 * */
	private boolean deletCopyFile(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		logger.info("�洢�������յ�ɾ��ָ��");
		String fileName = dis.readUTF();
		logger.info("�洢���������յ��ļ�ȫ��:" + fileName);
		String path = fileStorage.StrogeServer.FileStorage.getStorageDir()
				+ "//" + fileName;
		File file = new File(path);
		if (file.delete()) {
			logger.info("�洢������ɾ���ļ��ɹ�");
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
		case 'u':// �ϴ�ָ��
			recieveFile(socket, dis, dos);
			break;
		case 'd':// ����ָ��
			sendFile(socket, dis, dos);
			break;
		case 'r':// ɾ��ָ��
			deletFile(socket, dis, dos);
			break;
		case 'c'://�����ļ�ָ��
			copyFile(socket,dis,dos);
			break;
		case 'a'://ɾ�����ݽڵ��е��ļ�
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
