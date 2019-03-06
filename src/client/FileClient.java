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
	 * ����
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
		logger.info("�ͻ��˷����ļ�uuid���ļ�������");
		Boolean result=dis.readBoolean();
		if(result==false)
		{
			logger.error("���ҵ�uuid������");
			return false;
		}
		int FileStoragyNum=dis.readInt();
		//��ȡ���õ��ļ�����������Ŀ
		if(FileStoragyNum==0)
		{
			logger.error("û�п��õ��ļ�������");
			return false;
		}
		if(FileStoragyNum==2){
		String fileName = dis.readUTF();
		logger.info("�ͻ��˽��յ��ļ����������͵��ļ�����:" + fileName);
		long size = dis.readLong();
		logger.info("�ͻ��˽��յ��ļ����������͵��ļ���С:" + size);
		File file = new File("E:\\work\\buffer\\"+fileName);
		if (file.exists()) {
			String newName = file.getName().substring(0,
					file.getName().indexOf('.'))
					+ "(1)"
					+ file.getName().substring(file.getName().indexOf('.'));
			System.out.println("���ļ��Ѵ��ڣ��Զ�������Ϊ" + newName);
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
		logger.info("�ͻ��˿�ʼ�����ļ����������͵��ļ�");
		logger.info("�ͻ��˿�ʼ�����ļ����������͵��ļ�");
		// �ļ�����
		long passedLen = 0;// ��ǰһ�������С
		int bufferSize = 8192;// ��������С
		byte[] buf = new byte[bufferSize];// ������
		try {
			while (true) {
				int read = 0;
				if (dis != null) {
					read = dis.read(buf);
				}
				if (read == -1) {
					break;
				}
				logger.info("�Ѿ�����:"+passedLen/(size*1.0)*100+"%");
				passedLen += read;
				fos.write(buf, 0, read);
			}
		} catch (SocketException e) {
			logger.error("�ͻ�����������������жϣ�������ֹ���ӱ��ݷ���������");
			// �ر���Դ
			dos.close();
			dis.close();
			boolean b=file.delete();
			if(b)
			{
				logger.info("ɾ��֮ǰ���ļ��ɹ�");
			}
			 socket2=new Socket(copyIP, copyPort);
			 dos=new DataOutputStream(socket2.getOutputStream());
			  dis=new DataInputStream(socket2.getInputStream());
			 fos = new FileOutputStream(file);
			dos.writeChar('d');
			dos.writeUTF(fileUUID);
			dos.flush();
			logger.info("�ͻ��˿�ʼ�����ļ����������͵��ļ�");
			logger.info("�ͻ��˿�ʼ�����ļ����������͵��ļ�");
			// �ļ�����
			 passedLen = 0;// ��ǰһ�������С
			 bufferSize = 8192;// ��������С
			 buf = new byte[bufferSize];// ������
			try {
				while (true) {
					int read = 0;
					if (dis != null) {
						read = dis.read(buf);
					}
					if (read == -1) {
						break;
					}
					logger.info("�Ѿ�����:"+passedLen/(size*1.0)*100+"%");
					passedLen += read;
					fos.write(buf, 0, read);
				}
			} catch (SocketException e1) {
				logger.error("�ͻ�����������������жϣ�������ֹ");
			// �ر���Դ			
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
				logger.info("�ͻ��˽����ļ��ɹ�");
				return true;
			} else {
				logger.info("�ͻ��˽����ļ�ʧ��");
				return false;
			}
		}
		fos.close();
		dos.close();
		dis.close();
		logger.info("������ɿ�ʼ��ѹ");
		ZipUtils.unzip(file.getAbsolutePath(), "e:\\work\\client\\"+file.getName());
		logger.info("��ѹ���");
		if (passedLen == size) {
			logger.info("�ͻ��˽����ļ��ɹ�");
			return true;
		} else {
			logger.info("�ͻ��˽����ļ�ʧ��");
			return false;
		}
		}
		if(FileStoragyNum==1)
		{
			String fileName = dis.readUTF();
			logger.info("�ͻ��˽��յ��ļ����������͵��ļ�����:" + fileName);
			long size = dis.readLong();
			logger.info("�ͻ��˽��յ��ļ����������͵��ļ���С:" + size);
			File file = new File("E:\\work\\buffer\\"+fileName);
			if (file.exists()) {
				String newName = file.getName().substring(0,
						file.getName().indexOf('.'))
						+ "(1)"
						+ file.getName().substring(file.getName().indexOf('.'));
				System.out.println("���ļ��Ѵ��ڣ��Զ�������Ϊ" + newName);
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
			logger.info("�ͻ��˿�ʼ�����ļ����������͵��ļ�");
			logger.info("�ͻ��˿�ʼ�����ļ����������͵��ļ�");
			// �ļ�����
			long passedLen = 0;// ��ǰһ�������С
			int bufferSize = 8192;// ��������С
			byte[] buf = new byte[bufferSize];// ������
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
				logger.error("�ͻ�����������������жϣ�������ֹ");
			// �ر���Դ			
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
			logger.info("������ɿ�ʼ��ѹ");
			ZipUtils.unzip(file.getAbsolutePath(), "e:\\work\\client\\"+file.getName());
			logger.info("��ѹ���");
			if (passedLen == size) {
				logger.info("�ͻ��˽����ļ��ɹ�");
				return true;
			} else {
				logger.info("�ͻ��˽����ļ�ʧ��");
				return false;
			}
		}
		return true;
	}
	/**
	 * ����uuidɾ���ļ�
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
		logger.info("�ͻ��˷����ļ�uuid���ļ�������ɾ��");
		Boolean result0=dis.readBoolean();
		if(result0==false)
		{
			logger.error("û�ж�Ӧ���ļ���ɾ��ʧ��");
			return false;
		}
		Boolean result1=dis.readBoolean();
		if(result1==false)
		{
			logger.error("�洢�������ر��޷�����ɾ��");
			return false;
		}
		logger.info("�洢�������������п���ɾ��");
		String mainIp=dis.readUTF();
		Integer mainPort=dis.readInt();//��ȡ���ڵ�ĵ�ַ��IP��ַ
		String copyIp=dis.readUTF();
		Integer copyPort=dis.readInt();//��ȡ���ݽڵ�ĵ�ַ��IP��ַ
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
			logger.error("ɾ���ڵ����");
		}
		return false;
	}
	/**
	 * �ϴ�
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public boolean upload(String filePath) throws IOException {
		dos.writeChar('u');// �ϴ��ļ�ָ��
		dos.flush();
		logger.info("�ͻ��� �����ϴ�ָ����ļ�������");
		File file0 = new File(filePath);
		if (!file0.exists()) {
			logger.error("�ļ�������");
			throw new FileNotExist("��ǰ�ļ����²����ڴ��ļ�");
		} else if (!file0.isFile()) {
			logger.info(filePath + "����һ���ļ�");
			throw new IsNotFileException(filePath + "����һ���ļ�");
		}
		logger.info("��ʼ����ѹ��");
		File file=new File(file0.getAbsolutePath()+"0");
		file.createNewFile();
		ZipUtils.zip(file0.getAbsolutePath(), file0.getName()+"0");
		logger.info("����ѹ�����");
		dos.writeLong(file.length());// ���ļ���С�����ļ���������ȷ���Ƿ�����ϴ�
		dos.flush();
		logger.info("�ͻ��˷����ļ���С���ļ�������");
		boolean result = dis.readBoolean();// �ļ�������������Ϣ���߿ͻ����Ƿ���ϴ�
		if (!result) {
			String erroInfo = dis.readUTF();// ����������Ϣ
			logger.error(erroInfo);
			throw new StorageServerInvalibException("û�п��õĴ洢������");
		}
		logger.info("����������ȷ����Ϣ�����ϴ�");
		dos.writeUTF(file0.getName());// ���ļ����ַ��͸�������
		String mainIp=dis.readUTF();
		Integer mainPort=dis.readInt();//��ȡ���ڵ�ĵ�ַ��IP��ַ
		String copyIp=dis.readUTF();
		Integer copyPort=dis.readInt();//��ȡ���ݽڵ�ĵ�ַ��IP��ַ
		final String filename=dis.readUTF();//��ȡ���ɵ��ļ�����
		logger.info("���������ɵ��ļ�����Ϊ"+filename);
		dos.flush();
		dis.close();
		dos.close();
		socket.close();
		logger.info("�ͻ��˷����ļ����Ƹ��ļ�������");
		logger.info("�ͻ��˿�ʼ�ϴ��ļ����ļ�������");
		// �ļ�����
		Socket socket1=new Socket(mainIp, mainPort);
		//���ļ��������������ӡ�
		DataInputStream dis0=new DataInputStream(socket1.getInputStream());
		DataOutputStream dos0=new DataOutputStream(socket1.getOutputStream());
		dos0.writeChar('u');
		dos0.writeLong(file.length());
		dos0.writeUTF(filename);
		logger.info("�ļ�����������ļ����ֺʹ�С");
		dos0.writeUTF(copyIp);
		dos0.writeInt(copyPort);
		logger.info("�ļ���������ñ��ݽڵ���Ϣ");
		int bufferSize = 4096;// ��������С
		byte[] buf = new byte[bufferSize];// ������
		long passedlen = 0; // �Ѵ����С
		int len = 0; // ÿ����read��ȡ���ص�ֵ���������ϴ����ֽ���
		long size = file.length();// �ļ���С
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
			dos.writeChar('u');// �ϴ��ļ�ָ��
			logger.error("��������ͻ���֮������ӶϿ��������ش����ļ�");
			dos.writeLong(file.length());// ���ļ���С�����ļ���������ȷ���Ƿ�����ϴ�
			dos.flush();
			logger.info("�ͻ��˷����ļ���С���ļ�������");
			result = dis.readBoolean();// �ļ�������������Ϣ���߿ͻ����Ƿ���ϴ�
			if (!result) {
				String erroInfo = dis.readUTF();// ����������Ϣ
				logger.error(erroInfo);
				throw new StorageServerInvalibException("û�п��õĴ洢������");
			}
			logger.info("����������ȷ����Ϣ�����ϴ�");
			dos.writeUTF(file.getName());// ���ļ����ַ��͸�������
			 mainIp=dis.readUTF();
			 mainPort=dis.readInt();//��ȡ���ڵ�ĵ�ַ��IP��ַ
			 System.out.println(mainPort);
			 copyIp=dis.readUTF();
			 copyPort=dis.readInt();//��ȡ���ݽڵ�ĵ�ַ��IP��ַ
			String filename0=dis.readUTF();//��ȡ���ɵ��ļ�����
			logger.info("���������ɵ��ļ�����Ϊ"+filename0);
			dos.flush();
			dis.close();
			dos.close();
			socket.close();
			logger.info("�ͻ��˷����ļ����Ƹ��ļ�������");
			logger.info("�ͻ��˿�ʼ�ϴ��ļ����ļ�������");
			// �ļ�����
			dos.flush();
			 socket1=new Socket(mainIp, mainPort);
			  dis0=new DataInputStream(socket1.getInputStream());
			  dos0=new DataOutputStream(socket1.getOutputStream());
			  dos0.writeChar('u');
			dos0.writeLong(size);
			dos0.writeUTF(filename0);
			logger.info("�ļ�����������ļ����ֺʹ�С");
			dos0.writeUTF(copyIp);
			dos0.writeInt(copyPort);
			logger.info("�ļ���������ñ��ݽڵ���Ϣ");
			 bufferSize = 4096;// ��������С
			 buf = new byte[bufferSize];// ������
			 passedlen = 0; // �Ѵ����С
			 len = 0; // ÿ����read��ȡ���ص�ֵ���������ϴ����ֽ���
			 size = file.length();// �ļ���С
			 fis = new FileInputStream(file);// ��Ҫ�ϴ����ļ���
			 bis = new BufferedInputStream(fis);
			final long sector0 = size / 100;
			sectorLen = 0;
			try {
				int percentage = 0;// �Ѵ���ٷֱ�
				while (passedlen < size) {
					len = bis.read(buf);
					passedlen += len;
					sectorLen += len;
					if (sectorLen >= sector0) {
						sectorLen = 0;
						percentage++;
						logger.info("�Ѵ���%" + percentage);
					}
					dos0.write(buf, 0, len);
					dos0.flush();
				}
				logger.info("�Ѵ���%" + 100);
			}catch(IOException exception){
				exception.printStackTrace();
				logger.error("�ٴγ��ִ���ֹͣ����");
			}
			fis.close();
			dis0.close();
			dos0.close();
			socket1.close();
			return false;
		} finally {
			// �ر���Դ
			fis.close();
			dis0.close();
			dos0.close();
			socket1.close();
		}
		if (passedlen == size) {
			logger.info("�ͻ����ļ��ϴ����");
			return true;
		} else {
			logger.error("�ͻ����ļ��ϴ�ʧ��");
			return false;
		}
	}
public static void main(String[] args) throws UnknownHostException, IOException {
	Logger logger = Logger.getLogger(FileClient.class);
	String[] a={"download","150051320"};
	if (a.length < 2) {
		throw new WrongArgumentException("������������");
	}
	FileClient client = new FileClient();
	if (a[0].trim().equals("upload")) {
		if (client.upload(a[1])) {
			logger.info("�ļ��ϴ��ɹ�");
		} else {
			logger.info("�ļ��ϴ�ʧ��");
		}
	} else if (a[0].trim().equals("download")) {
		client.download(a[1]);
	} else if (a[0].trim().equals("remove")) {
		client.remove(a[1]);
	}  else {
		throw new WrongArgumentException("��������");
	}
}
}