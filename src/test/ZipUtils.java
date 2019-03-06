package test;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ͨ��Java��Zip���������ʵ��ѹ���ͽ�ѹ�ļ�
 */
public final class ZipUtils {
    /**
     * ѹ���ļ�
     */
    public static void zip(String filePath,String zipName) {
        File target = null;
        File source = new File(filePath);
        try {
			new File(zipName).createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if (source.exists()) {
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // ɾ���ɵ��ļ�
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // ��Ӷ�Ӧ���ļ�Entry
                addEntry("/", source, zos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                closeQuietly(zos, fos);
            }
        }
    }

    /**
     * ɨ������ļ�Entry
     *
     * @param base
     *            ��·��
     *
     * @param source
     *            Դ�ļ�
     * @param zos
     *            Zip�ļ������
     * @throws IOException
     */
    private static void addEntry(String base, File source, ZipOutputStream zos)
            throws IOException {
        // ��Ŀ¼�ּ������磺/aaa/bbb.txt
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // �ݹ��г�Ŀ¼�µ������ļ�������ļ�Entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[1024 * 10];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                closeQuietly(bis, fis);
            }
        }
    }

    /**
     * ��ѹ�ļ�
     */
    public static void unzip(String filePath,String targetname) {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            try {
                zis = new ZipInputStream(new FileInputStream(source));
                ZipEntry entry = null;
                while ((entry = zis.getNextEntry()) != null
                        && !entry.isDirectory()) {
                    File target = new File(targetname);
                    // д���ļ�
                    bos = new BufferedOutputStream(new FileOutputStream(target));
                    int read = 0;
                    byte[] buffer = new byte[1024];
                    while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, read);
                    }
                    bos.flush();
                }
                zis.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                closeQuietly(zis, bos);
            }
        }
    }
    /**
     * �ر�һ������������
     *
     * @param closeables
     *            �ɹرյ��������б�
     * @throws IOException
     */
    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
    }

    /**
     * �ر�һ������������
     *
     * @param closeables
     *            �ɹرյ��������б�
     */
    public static void closeQuietly(Closeable... closeables) {
        try {
            close(closeables);
        } catch (IOException e) {
            // do nothing
        }
    }
    public static void main(String[] args) {
//        ZipUtils.zip("E:\\work\\server\\node1\\1499954325791.mkv","1499954325791");

     ZipUtils.unzip("E:\\work\\server\\node1\\1499954325791","E:\\work\\client\\1499954325791.mkv");
    }
}