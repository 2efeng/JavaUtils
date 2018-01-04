package com.hzf.utils.HDFSUtil;

import com.hzf.utils.ConfigUtil.ConfigUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HDFSUtil {

    private HDFSUtil() {
    }

    /**
     * 配置FileSystem
     */
    private static FileSystem getFileSystem() throws Exception {
        String ip = ConfigUtil.getProperty("hadoopIP");
        String port = ConfigUtil.getProperty("hadoopPort");
        String hadoopUserName = ConfigUtil.getProperty("hadoopUserName");
        String url = "hdfs://" + ip + ":" + port;
        System.setProperty("HADOOP_USER_NAME", hadoopUserName);
        Configuration config = new Configuration();
        config.set(FileSystem.FS_DEFAULT_NAME_KEY, url);
        return FileSystem.get(config);
    }

    public static Path getWorkDir(FileSystem fs) {
        return fs.getWorkingDirectory();
    }


    /**
     * 检查文件or路径是否存在
     *
     * @param file 文件or路径
     */
    public static boolean checkFileIsExist(FileSystem fs, String file) throws Exception {
        file = normFileName(file);
        Path path = new Path(getWorkDir(fs) + file);
        return fs.exists(path);
    }

    /**
     * 删除所有文件
     */
    public static boolean deleteAll(FileSystem fs) throws Exception {
        Path workDir = fs.getWorkingDirectory();
        return fs.delete(workDir, true);
    }

    public static void delete(FileSystem fs, String path) throws Exception {
        Path workDir = fs.getWorkingDirectory();
        Path delFilePath = new Path(workDir + "/" + path);
        if (fs.exists(delFilePath)) {
            fs.delete(delFilePath, true);
            System.out.println(path + " have delete!");
        } else {
            System.out.println("the file isn't exists");
        }
    }


    public static InputStream getHDFSFileStream(FileSystem fs, String file) throws Exception {
        Path path = new Path(getWorkDir(fs) + "/" + normFileName(file));
        return fs.open(path);// 获取文件流
    }

    /**
     * 下载hdfs文件到本地
     *
     * @param filePath 路径/文件名
     * @param local    本地路径
     */
    public static void downloadFile2local(FileSystem fs, String ssid, String filePath, String local) throws Exception {
        FSDataInputStream FSin = null;
        FileOutputStream out = null;
        try {
            filePath = normFileName(filePath);
            Path path = new Path(getWorkDir(fs) + filePath);
            FSin = fs.open(path);// 获取文件流
            byte[] data = new byte[FSin.available()];
            out = new FileOutputStream(local);
            out.write(data);
        } finally {
            if (out != null) out.close();
            if (FSin != null) FSin.close();
        }
    }

    /**
     * 上传文件到hdfs
     *
     * @param filePath String hdfs 路径/文件名
     * @param in       InputStream 文件流
     */
    public static void uploadFile2hdfs(FileSystem fs, InputStream in, String filePath) throws Exception {
        FSDataOutputStream FSos = null;
        try {
            filePath = normFileName(filePath);
            byte[] content = new byte[in.available()];
            int len = in.read(content);
            Path path = new Path(getWorkDir(fs) + filePath);
            FSos = fs.create(path);
            FSos.write(content, 0, len);
        } finally {
            if (FSos != null) FSos.close();
            if (in != null) in.close();
        }
    }


    /**
     * 上传文件到hdfs
     *
     * @param localPath 本地文件
     * @param hdfsPath  hdfs路径/文件名
     */
    public static void uploadFile2hdfs(FileSystem fs, String localPath, String hdfsPath) throws Exception {
        InputStream in = new FileInputStream(localPath);
        uploadFile2hdfs(fs, in, hdfsPath);
    }

    /**
     * 创建文件
     */
    public static void createFile(FileSystem fs, String filePath, String fileName, String content) throws Exception {
        byte[] bytes = content.getBytes();
        Path workDir = fs.getWorkingDirectory();
        String dirFile = workDir + "/" + filePath + "/" + fileName;
        FSDataOutputStream os = null;
        try {
            os = fs.create(new Path(dirFile));
            os.write(bytes, 0, bytes.length);
        } finally {
            if (os != null) os.close();
        }
    }


    /**
     * 规范文件名
     */
    private static String normFileName(String file) {
        file = file.replace('\\', '/');
        if (!file.startsWith("/")) file = "/" + file;
        return file;
    }

    public static ByteArrayOutputStream getHDFSFileStream(FileSystem fs, Path path) throws Exception {
        FSDataInputStream in = fs.open(path);// 获取文件流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copyBytes(in, bos, 1024 * 8, false);
        in.close();
        return bos;
    }

    //将斜杠出错的文件名迁移
    private void move() throws Exception {
        FileSystem fs = getFileSystem();
        List<String> fileNames = new ArrayList<>();
        Path path = new Path(getWorkDir(fs) + "/" + "PDF");
        FileStatus[] fList = fs.listStatus(path);
        for (FileStatus file : fList) {
            String now = path.toString() + "/" + file.getPath().getName();
            String actual = file.getPath().toString();
            if (!now.equals(actual)) {
                String temp = actual.replace(path.toString() + "/", "");
                temp = temp.replace("/", "\\");
                actual = path.toString() + "/" + temp;
            }
            if (actual.contains("\\")) fileNames.add(actual);
        }
        for (String file : fileNames) {
            Path oldPath = new Path(file);
            Path newPath = new Path(file.replace("\\", "/"));
            InputStream in = fs.open(oldPath);
            HDFSUtil.uploadFile2hdfs(fs, in, newPath.toString().replace(getWorkDir(fs).toString() + "/", ""));
        }
    }

    public static void main(String[] args) throws Exception {
        FileSystem fs = getFileSystem();

    }

}