package test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import util.Tool;

import java.io.*;
import java.security.Key;
import java.security.SecureRandom;

public class IN {

    public static void main(String[] args) {

        String filename = "f:\\[QINDOU.ME]勇敢的心1995.HR1024中字.mkv";
//       SecretKey key = IN.generateAESKey(256);
//       try {
//		Tool.serialize(key, new FileOutputStream(new File("e:\\work\\buffer\\key.txt")));
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
        SecretKey key = null;
		try {
			key = (SecretKey) Tool.deserialize(new FileInputStream(new File("e:\\work\\buffer\\key.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       System.out.println(key.toString());
       // AES加密文件内容并输出到另一文件
        encryptFileByAES(filename, "E:\\work\\buffer\\1.txt", key);
//        //AES解密文件内容并输出到另一文件
////        decryptFileByAES("E:\\work\\buffer\\1.txt", "E:\\work\\buffer\\2.txt", key);
    }

    public static boolean decryptFileByAES(String destname, String srcname, SecretKey key) {
        try {
            toFile(srcname,decryptByAES(toByteArrays(destname),key));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean encryptFileByAES(String srcname, String destname, Key key)  {
        try {
            toFile(destname,encryptByAES(toByteArrays(srcname), key));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static byte[] toByteArrays(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    public static void toFile(String filename, byte[] out) throws IOException{
        FileOutputStream fos = new FileOutputStream(filename);

        fos.write(out);
        fos.close();
    }

    public static SecretKey generateAESKey(int keySize) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(keySize, new SecureRandom());
            return kgen.generateKey();
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] encryptByAES(byte[] input, Key aesKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] e1 = cipher.doFinal(input);
            return e1;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] decryptByAES(byte[] input, Key aesKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            return cipher.doFinal(input);
        } catch (Exception e) {
            return null;
        }
    }
}
