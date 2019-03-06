package guiObserver;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.FileNode;
/*
 * 定时观察文件节点
 */
public class FileNodeObserverByTimer extends JFrame {
	private Logger logger=Logger.getLogger(FileNodeObserverByTimer.class);
	 private DefaultTableModel model = null;
	   private JTable table = null;
	   private Integer time;//刷新的间隔
	   public FileNodeObserverByTimer(int time)
	   {
		   super("FileNodeObserverByTimer");
		   this.time=time;
		   String[][] datas = {};
		      String[] titles = { "文件编号", "文件名" ,"文件大小","主存储服务器IP","主存储服务器端口号","备份存储服务器IP","备份存储服务器端口号"};
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
			   dos.writeChar('o');
			   logger.info("和服务器建立成功");
			   String json=dis.readUTF();
			   logger.info("从服务器取值成功");
			   java.lang.reflect.Type type = new TypeToken<ArrayList<FileNode>>() {  
			   }.getType();  
			   ArrayList<FileNode> fileNodes=new Gson().fromJson(json,type );
			   model.setRowCount(0);
			   for (FileNode fileNode : fileNodes) {
				model.addRow(new String[]{fileNode.getUuid(),fileNode.getName(),new Long(fileNode.getSize()).toString(),fileNode.getMainServerIp(),new Integer(fileNode.getMainServerNode()).toString(),fileNode.getCopyServerIp(),new Integer(fileNode.getCopyServerNode()).toString()});
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
	   public static void main(String[] args) throws UnknownHostException, IOException {
		new FileNodeObserverByTimer(2);;
	}
}
