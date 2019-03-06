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
 * ��ʱ�۲��ļ��ڵ�
 */
public class FileNodeObserverByTimer extends JFrame {
	private Logger logger=Logger.getLogger(FileNodeObserverByTimer.class);
	 private DefaultTableModel model = null;
	   private JTable table = null;
	   private Integer time;//ˢ�µļ��
	   public FileNodeObserverByTimer(int time)
	   {
		   super("FileNodeObserverByTimer");
		   this.time=time;
		   String[][] datas = {};
		      String[] titles = { "�ļ����", "�ļ���" ,"�ļ���С","���洢������IP","���洢�������˿ں�","���ݴ洢������IP","���ݴ洢�������˿ں�"};
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
			   logger.info("�ͷ����������ɹ�");
			   String json=dis.readUTF();
			   logger.info("�ӷ�����ȡֵ�ɹ�");
			   java.lang.reflect.Type type = new TypeToken<ArrayList<FileNode>>() {  
			   }.getType();  
			   ArrayList<FileNode> fileNodes=new Gson().fromJson(json,type );
			   model.setRowCount(0);
			   for (FileNode fileNode : fileNodes) {
				model.addRow(new String[]{fileNode.getUuid(),fileNode.getName(),new Long(fileNode.getSize()).toString(),fileNode.getMainServerIp(),new Integer(fileNode.getMainServerNode()).toString(),fileNode.getCopyServerIp(),new Integer(fileNode.getCopyServerNode()).toString()});
			}
			   logger.info("�ļ���Ϣ���³ɹ�");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("�ͷ��������ӳ�������");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   public static void main(String[] args) throws UnknownHostException, IOException {
		new FileNodeObserverByTimer(2);;
	}
}
