package guiObserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.FileStorage;

public class FileStoragyObserverByRealTime extends JFrame{
	 private DefaultTableModel model = null;
	   private JTable table = null;
	   private Integer time;//刷新的间隔
	   private Logger logger=Logger.getLogger(FileStoragyObserverByTimer.class);
	   public FileStoragyObserverByRealTime(int time)
	   {
		   super("FileStoragyObserverByTimer");
		   this.time=time;
		   String[][] datas = {};
		      String[] titles = { "服务器名字", "IP地址" ,"端口号","最大存储空间","实际存储空间","剩余存储空间","文件数","是否可用","存储路径"};
		      model = new DefaultTableModel(datas, titles);
		      table = new JTable(model);
		      add(new JScrollPane(table));
		      setDatas();
		      setSize(500, 400);
		      setLocationRelativeTo(null);
		      setDefaultCloseOperation(EXIT_ON_CLOSE);
		      setVisible(true);
		      new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					setDatas();
				}
			}, time*1000,time*1000);
	   }
	   public void setDatas() 
	   {
		   Socket socket;
		try {
			socket = new Socket("localhost", 4321);
			 DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
			   DataInputStream dis=new DataInputStream(socket.getInputStream());
			   dos.writeChar('z');
			   logger.info("和服务器建立成功");
			   String json=dis.readUTF();
			   logger.info("从服务器取值成功");
			   java.lang.reflect.Type type = new TypeToken<ArrayList<FileStorage>>() {  
			   }.getType();  
			   ArrayList<FileStorage> fileStorages=new Gson().fromJson(json,type );
			   model.setRowCount(0);
			   for (FileStorage fileStorage : fileStorages) {
				model.addRow(new String[]{fileStorage.getName(),fileStorage.getIP(),new Integer(fileStorage.getPort()).toString(),new Double(fileStorage.getMaxVolume()).toString(),new Double(fileStorage.getRealVolume()).toString(),new Double(fileStorage.getLeftVolume()).toString(),new Integer(fileStorage.getFileNum()).toString(),new Boolean(fileStorage.getIsAlive()).toString(),fileStorage.getStorageDir()});
			}
			   logger.info("文件信息更新成功");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("和服务器连接出现问题");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   public static void main(String[] args) {
		new FileStoragyObserverByTimer(3);
	}
}
