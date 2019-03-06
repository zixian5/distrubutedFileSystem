package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import model.FileNode;
import model.FileStorage;
import util.StorageServerUtil;

public class FileServer {
	public static List<FileNode> fileNodes=StorageServerUtil.readObjectForList();
	public static HashMap<String, Timer> timerMap = new HashMap<String, Timer>();
	public static void main(String[] args) throws FileNotFoundException, IOException {
		FileStorage[] servers = StorageServerUtil
				.getAllStorageServerFromDir("properties");
		for (int i = 0; i < servers.length; i++) {
			// 为每一个storageServer启动一个计时器
			StorageServerUtil.addStorageServerToSystem(servers[i]);
			timerMap.put(servers[i].getName(), new Timer());
			timerMap.get(servers[i].getName()).schedule(
					new FileServerTimerTask(servers[i].getName()), 1000 * 6);
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					new UDPServer(4321);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		new NwServer(4321, new ThreadPoolSupport(new ServerFileProtocol()));
	}
}




