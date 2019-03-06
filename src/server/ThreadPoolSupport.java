package server;

import java.util.*; //����ĳ�������滻ThreadSupport.java
import java.net.Socket;

public class ThreadPoolSupport implements IOStrategy { // ThreadPoolSupport.java
	private ArrayList threads = new ArrayList();
	private final int INIT_THREADS = 50;
	private final int MAX_THREADS = 100;
	private IOStrategy ios = null;

	public ThreadPoolSupport(IOStrategy ios) { // �����̳߳�
		this.ios = ios;
		for (int i = 0; i < INIT_THREADS; i++) {
			IOThread t = new IOThread(ios); // ����Э����󣬵��ǻ�û��socket
			t.start(); // �����̣߳������߳������wait
			threads.add(t);
		}
		try {
			Thread.sleep(300);
		} catch (Exception e) {
		} // �ȴ��̳߳ص��̶߳������С�
	}

	public void service(Socket socket) { // �����̳߳أ��ҵ�һ�����е��̣߳�
		IOThread t = null; // �ѿͻ��˽�������������
		boolean found = false;
		for (int i = 0; i < threads.size(); i++) {
			t = (IOThread) threads.get(i);
			if (t.isIdle()) {
				found = true;
				break;
			}
		}
		if (!found) // �̳߳��е��̶߳�æ��û�а취�ˣ�ֻ�д���
		{ // һ���߳��ˣ�ͬʱ��ӵ��̳߳��С�
			t = new IOThread(ios);
			t.start();
			try {
				Thread.sleep(300);
			} catch (Exception e) {
			}
			threads.add(t);
		}
		t.setSocket(socket); // ���������˵�socket���󴫵ݸ�������е��߳�
	} // ���俪ʼִ��Э�飬��ͻ����ṩ����
}
