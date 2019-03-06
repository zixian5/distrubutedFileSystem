package guiObserver;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observer;

import javax.swing.JTextArea;

public class ObserverGui {
	private Frame f;
	private TextArea ta;
	private JTextArea ta1;
	private JTextArea ta2;
	private JTextArea ta3;
	private JTextArea ta4;
	private JTextArea ta5;
	private Button but1;
	private Button but2;
	private Button but3;
	private Button but4;
      ObserverGui() {
		// TODO Auto-generated method stub
    	init();
	}
    public void init()
    {
    	System.out.println(1);
    	f=new Frame("ObserverGui");
    	f.setBounds(400,150,600,500);
    	f.setLayout(new FlowLayout());
    	ta=new TextArea(3,70);
    	ta.setFont(new Font("宋体",Font.BOLD,27));
    	ta.setText("\n\n"+"                            请选择查看方式：");
    	ta3=new JTextArea(1,70);
    	ta4=new JTextArea(1,20);
    	ta5=new JTextArea(1,20);
    	ta3.setText("");
    	ta1=new JTextArea(1,20);
    	ta1.setText("1.定时查看文件信息");
    	ta1.setFont(new Font("宋体",Font.BOLD,25));
    	ta2=new JTextArea(1,20);
    	ta2.setText("2.定时查看存储节点信息");
    	ta2.setFont(new Font("宋体",Font.BOLD,25));
    	ta4.setText("3.实时查看文件信息");
    	ta4.setFont(new Font("宋体",Font.BOLD,25));
    	ta5.setText("4.实时查看存储节点信息");
    	ta5.setFont(new Font("宋体",Font.BOLD,25));
    	but1=new Button("选择");
    	but2=new Button("选择");
    	but3=new Button("选择");
    	but4=new Button("选择");
    	myEvent();// 加载事件处理
    	f.add(ta);
    	f.add(ta3);
    	f.add(ta1);
    	f.add(but1);
    	f.add(ta2);
    	f.add(but2);
    	f.add(ta4);
    	f.add(but3);
    	f.add(ta5);
    	f.add(but4);
    	f.setVisible(true);
    }
    public void myEvent()
    {
    	 f.addWindowListener(new WindowAdapter() {
    		 public void windowClosing(WindowEvent e) {
                 System.exit(0);

             }
		});
    	 but1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new FileNodeObserverByTimer(10);
			}
		});
    	 but2.addActionListener(new ActionListener() {
 			
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				// TODO Auto-generated method stub

 				new FileStoragyObserverByTimer(10);
 			}
 		});
    	 but3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new FileNodeObserverByRealTime(3);
			}
		});
    	 but4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new FileStoragyObserverByRealTime(10);
			}
		});
    }
    public static void main(String[] args) {
		new ObserverGui();
	}
}
