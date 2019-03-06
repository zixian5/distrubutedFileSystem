package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {
	public UDPServer(int port) throws IOException
	{
	    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		DatagramSocket socket=new DatagramSocket(port);
		System.out.println("UDPserver is ready");
		while(true)
		{
		    byte[] data=new byte[5];	    
			DatagramPacket packet=new DatagramPacket(data, data.length);
			socket.receive(packet);
			String name=new String(data);
			fixedThreadPool.execute(new UDPProtocol(name));
		}
	}
}
