package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import model.FileNode;
import model.FileStorage;
import server.FileServer;


public class StorageServerUtil {

	private static Logger logger = Logger.getLogger(StorageServerUtil.class);

	/**
	 * 获取目录下所有文件和目录的名字
	 * 
	 * @param dirPath
	 * @return
	 */
	public static String[] getAllFileNamesOfDir(String dirPath) {
		dirPath = System.getProperty("user.dir") + "\\" + dirPath;
		File file = new File(dirPath);
		File[] fileList = file.listFiles();
		String[] nameList = new String[fileList.length];
		for (int i = 0; i < fileList.length; i++) {
			nameList[i] = fileList[i].getName();
		}
		return nameList;
	}

	/**
	 * 读取一个存储服务器的配置文件组装成一个实例
	 * 
	 * @param fileName
	 * @return
	 */
	public static FileStorage getStorageServerFromFile(String fileName) {
		Properties properties = Tool.loadProperties(fileName);
		FileStorage ss = new FileStorage();
		ss.setName(properties.getProperty("NodeName"));
		String maxVolume = properties.getProperty("Volume");
		String num = "";
		double volume = 0;
		for (int i = 0; i < maxVolume.length(); i++) {
			if (Character.isDigit(maxVolume.charAt(i))) {
				num += maxVolume.charAt(i);
				continue;
			}
			volume = Integer.parseInt(num);
			switch (maxVolume.charAt(i)) {
			case 'B':
				break;
			case 'K':
				volume = volume * 1024;
				break;
			case 'M':
				volume = volume * 1024 * 1024;
				break;
			case 'G':
				volume = volume * 1024 * 1024 * 1024;
				break;
			default:
				break;
			}
			break;
		}
		ss.setRealVolume(volume);
		ss.setMaxVolume(volume);
		ss.setPort(Integer.valueOf(properties.getProperty("NodePort")));
		ss.setIP(properties.getProperty("NodeIP"));
		ss.setStorageDir(properties.getProperty("RootFolder"));
		String StorageDir=properties.getProperty("RootFolder");
		File file=new File(StorageDir);
		ss.setFileNum(file.listFiles().length);
		ss.setLeftVolume(volume-getFileLength(file.getAbsolutePath()));
		return ss;
	}
	/*
	 * 获取一个文件夹的内存大小
	 */
	public static Long getFileLength(String filepath)
	{
		long sum=(long)0;
		File file=new File(filepath);
		File[] files=file.listFiles();
		for (File file2 : files) {
			sum+=file2.length();
		}
		return sum;
	}
	/**
	 * 读取一个目录下的所有存储服务器的配置文件组装成实例返回
	 * 
	 * @param dirPath
	 * @return
	 */
	public static FileStorage[] getAllStorageServerFromDir(String dirPath) {
		String[] fileName = getAllFileNamesOfDir(dirPath);
		FileStorage[] servers = new FileStorage[fileName.length];
		for (int i = 0; i < fileName.length; i++) {
			servers[i] = getStorageServerFromFile(dirPath+"//"+fileName[i]);
		}
		return servers;
	}

	/**
	 * 添加存储服务器到系统
	 * 
	 * @param ss
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static synchronized boolean addStorageServerToSystem(FileStorage ss) throws FileNotFoundException, IOException {
		String path="e:\\work\\server\\server\\"+ss.getName()+".txt";
		File file=new File(path);
		ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(ss);
		oos.flush();
		oos.close();
		return true;
	}
	/**
	 * 根据名字查找存储服务器
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static synchronized FileStorage findByName(String name) throws ClassNotFoundException, IOException
	{
		String path="e:\\work\\server\\server\\"+name+".txt";
		File file=new File(path);
		FileInputStream fis = new FileInputStream(file);  
		   ObjectInputStream ois = new ObjectInputStream(fis); 
		   FileStorage fileStorage=(FileStorage)ois.readObject();
		   return fileStorage;
	}
	/*
	 * 获取一个文件下的所有可用文件存储器
	 * */
	public static synchronized java.util.List<FileStorage> getAllLiveFileStorage()
	{
		String path="e:\\work\\server\\server";
		List<FileStorage> fileStorages=new ArrayList<>();
		File file=new File(path);
		String[] files=file.list();
		for (String string : files) {
			try {
				if(findByName(string.split("\\.")[0]).getIsAlive())
				{
					fileStorages.add(findByName(string.split("\\.")[0]));
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				fileStorages=null;
			}
		}
		return fileStorages;
	}
	/*
	 * 获取剩余地址最大的两个文件服务器，
	 * 其中下标0是最大的，1是第二大的
	 * */
	public static synchronized List<FileStorage> getMaxDoubleFileStorage()
	{
		List<FileStorage> fileStorages=getAllLiveFileStorage();
		List<FileStorage> MaxDouble=new ArrayList<>();
		int num=0;
		int i=0;
		System.out.println(fileStorages);
		for (FileStorage fileStorage : fileStorages) {
			if(fileStorage.getLeftVolume()-fileStorages.get(num).getLeftVolume()>0)
			{
				System.out.println(fileStorage.getLeftVolume()+"-----"+fileStorages.get(num).getLeftVolume());
				num=i;
			}
			i++;
		}
		MaxDouble.add(fileStorages.get(num));
		System.out.println(MaxDouble);
		fileStorages.remove(num);
		int  num0=0;
		int  i0=0;
		for (FileStorage fileStorage : fileStorages) {
			if(fileStorage.getLeftVolume()>fileStorages.get(num0).getLeftVolume())
			{
				num0=i0;
			}
			i0++;
		}
		MaxDouble.add(fileStorages.get(num0));
		return MaxDouble;
	}
	/*
	 * 依照时间生成独立的文件标识
	 * */
	public static synchronized String  getUUID()
	{
		return new Long(new Date().getTime()).toString();
	}
	/**
     * 序列化,List
     */
    public static synchronized <T> boolean writeObject(List<T> list)
    {
    	File file=new File("E:\\work\\server\\file\\filelist.txt");
        T[] array = (T[]) list.toArray();
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) 
        {
            out.writeObject(array);
            out.flush();
            return true;
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 反序列化,List
     */
    public static synchronized <E> List<E> readObjectForList()
    {
    	File file=new File("E:\\work\\server\\file\\filelist.txt");
        E[] object;
        if(file.length()==0)
        {
        	return new ArrayList<>();
        }
        try(ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) 
        {
            object = (E[]) out.readObject();
            List<E> list=new ArrayList<>( Arrays.asList(object));
            return list;
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
	public static void main(String[] args) throws IOException, ClassNotFoundException {
//		System.out.println(findByName("Node1"));
//		System.out.println(findByName("Node3"));
////		System.out.println(getAllLiveFileStorage());
//////		System.out.println(getMaxDoubleFileStorage());
////////		System.out.println(getUUID());
//	//	System.out.println(getFileLength("E:\\work\\server\\node3"));
//	//	List<FileNode> fileNodes=new ArrayList<>();
////		FileNode fileNode=new FileNode("1", "2", (long)1.0, "中文",1, "localhost", new Integer(1));
////		fileNodes.remove(fileNode);
////		writeObject(fileNodes);
//		System.out.println(readObjectForList());
		System.out.println(new Gson().toJson(FileServer.fileNodes));
	}

}
