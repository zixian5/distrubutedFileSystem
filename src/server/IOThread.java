package server;

//�������޸ĺ���߳��࣬�ó����ͬ�����ƣ�wait��notify�����ĵ���Ҫ��ϸ�
import java.net.*;

public class IOThread extends Thread {
	private Socket socket = null;
	private IOStrategy ios = null;

	public IOThread(IOStrategy ios) { // ��Ƚ���һ���е�IOThread��Ĺ��췽��
		this.ios = ios; // �кβ�ͬ��
	}

	public boolean isIdle() { // ���socket����Ϊ�գ���ô����̵߳�Ȼ�ǿ��е�
		return socket == null;
	}

	public synchronized void setSocket(Socket socket) {
		this.socket = socket; // ���ݸ�����������߳�һ�������񡱣�����������
		notify();
	}

	public synchronized void run() { // ���ͬ�����������Ǳ���ʲô�������ݣ�
		while (true) { // ������Ϊwait�������ñ���ӵ�ж�����
			try {
				wait(); // �����߳�������̽����������ȴ�״̬
				ios.service(socket); // �����Ѻ����̿�ʼִ�з���Э��
				socket = null; // ������������̷��ص�����״̬
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
