package test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

public class Test {
	public static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) throws UnknownHostException, IOException {
//		String filename = "D://a.txt";
        SecretKey key = IN.generateAESKey(128);
//        String path="E:\\work\\buffer\\1.txt";
//        IN.encryptFileByAES(filename, path, key);
//        ZipUtils.zip(path,"1");
//		String filename="E:\\work\\buffer\\1";
//		ZipUtils.unzip(filename,"E:\\work\\buffer\\2.txt");
//		SecretKey key = IN.generateAESKey(128);
		IN.decryptFileByAES("E:\\work\\buffer\\1.txt","E:\\work\\buffer\\3.txt", key);
	}
}
