package server;

import java.net.*;

public class NwServer { // NwServer.java����������������󣬲���������Socket����
	// ͨ��IOStrategy�ӿڴ��ݸ�ThreadSupport����
	public NwServer(int port, IOStrategy ios) { // ��������������߳���ִ��
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("server is ready");
			while (true) {
				Socket socket = ss.accept(); // ���������������
				ios.service(socket); // ���������˵�socket���󴫵ݸ�
			} // ThreadSupport����
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
