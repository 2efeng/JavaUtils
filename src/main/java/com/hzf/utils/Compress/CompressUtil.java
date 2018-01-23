package com.hzf.utils.Compress;

import com.hzf.utils.DateUtil.DateUtil;
import com.hzf.utils.HDFSUtil.HDFSUtil;
import org.apache.hadoop.fs.FileSystem;

import java.io.*;
import java.util.zip.*;

public class CompressUtil {

    public static void main(String[] args) throws Exception {

        String file = "ZIP/Test.zip";
        FileSystem fs = HDFSUtil.getFileSystem();
        InputStream in = HDFSUtil.getHDFSFileStream(fs, file);

        String fileName = "HBaseClient/Test/hadoop123.txt";
        byte[] bytes = readZipFile(in, fileName);

        String local = "C:\\Users\\zf.huang\\Desktop\\1234.zip";
        outFile(bytes, local);

    }

    /**
     * 不需要解压获取指定文件的byte
     */
    private static byte[] readZipFile(InputStream in, String filePath) throws Exception {
        if (in == null) return null;
        ZipInputStream zin = null;
        try {
            //filePath为空的时候返回整个压缩包的byte
            if (filePath == null || filePath.equals("")) return in2byte(in);
            zin = new ZipInputStream(in);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null)
                if (!ze.isDirectory() && ze.getName().equals(filePath))
                    return in2byte(zin);
            throw new Exception("File " + filePath + " does not exit in ZIP.");
        } finally {
            if (zin != null) zin.close();
            in.close();
        }
    }


    private static void compressFiles(String... fileNames) throws Exception {
        FileInputStream[] ins = new FileInputStream[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            ins[i] = new FileInputStream(fileNames[i]);
        }
        compressFiles(ins);
    }


    private static void compressFiles(InputStream... ins) throws Exception {
        String zipFileName = System.getProperty("user.dir")
                + File.separator + "zipDir"
                + File.separator + DateUtil.currentTimeStamp() + ".zip";
        File file = new File(zipFileName);
        if (!file.exists())
            System.out.println(file.createNewFile());

        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        zipFile(ins, zipOut);
        zipOut.close();
        fos.close();

    }

    private static void zipFile(InputStream[] ins, ZipOutputStream zipOut) {


    }


    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if (!sourceFile.exists()) {
            System.out.println("待压缩的文件目录：" + sourceFilePath + "不存在.");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName + ".zip" + "打包文件.");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (File sourceFile1 : sourceFiles) {
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFile1.getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFile1);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                //关闭流
                try {
                    if (null != bis) bis.close();
                    if (null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }


    private static byte[] in2byte(InputStream in) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) swapStream.write(b);
        byte[] bytes = swapStream.toByteArray();
        swapStream.close();
        return bytes;
    }

    private static void outFile(byte[] bytes, String local) throws Exception {
        FileOutputStream out = new FileOutputStream(local);
        out.write(bytes);
        out.close();
    }

}
