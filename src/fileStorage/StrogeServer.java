package fileStorage;

import java.util.Timer;

import model.FileStorage;
import server.NwServer;
import server.ThreadPoolSupport;
import util.StorageServerUtil;
public class StrogeServer {
	public  static FileStorage FileStorage;

	static {
		StrogeServer.FileStorage = StorageServerUtil
				.getStorageServerFromFile("properties/storage3.properties");
	}
	public static void main(String[] args) {
		new Timer().schedule(new StorageServerTimerTask(FileStorage.getName()), 1*1000, 5 * 1000);
		new NwServer(FileStorage.getPort(), new ThreadPoolSupport(
				new StorageFileProtocol()));
	}
}
