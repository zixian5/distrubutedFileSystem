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
	   private Integer time;//ˢ�µļ��
	   private Logger logger=Logger.getLogger(FileStoragyObserverByTimer.class);
	   public FileStoragyObserverByRealTime(int time)
	   {
		   super("FileStoragyObserverByTimer");
		   this.time=time;
		   String[][] datas = {};
		      String[] titles = { "����������", "IP��ַ" ,"�˿ں�","���洢�ռ�","ʵ�ʴ洢�ռ�","ʣ��洢�ռ�","�ļ���","�Ƿ����","�洢·��"};
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
			   logger.info("�ͷ����������ɹ�");
			   String json=dis.readUTF();
			   logger.info("�ӷ�����ȡֵ�ɹ�");
			   java.lang.reflect.Type type = new TypeToken<ArrayList<FileStorage>>() {  
			   }.getType();  
			   ArrayList<FileStorage> fileStorages=new Gson().fromJson(json,type );
			   model.setRowCount(0);
			   for (FileStorage fileStorage : fileStorages) {
				model.addRow(new String[]{fileStorage.getName(),fileStorage.getIP(),new Integer(fileStorage.getPort()).toString(),new Double(fileStorage.getMaxVolume()).toString(),new Double(fileStorage.getRealVolume()).toString(),new Double(fileStorage.getLeftVolume()).toString(),new Integer(fileStorage.getFileNum()).toString(),new Boolean(fileStorage.getIsAlive()).toString(),fileStorage.getStorageDir()});
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
	   public static void main(String[] args) {
		new FileStoragyObserverByTimer(3);
	}
}
