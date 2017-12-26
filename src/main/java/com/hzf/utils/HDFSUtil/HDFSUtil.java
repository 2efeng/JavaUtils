package com.hzf.utils.HDFSUtil;

import com.hzf.utils.ConfigUtil.ConfigUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class HDFSUtil {

    private static volatile HDFSUtil hdfsUtil = null;
    private static volatile FileSystem fs = null;
    private static volatile Path workDir = null;

    private HDFSUtil() {
    }

    private static synchronized void syncInit() throws Exception {
        if (hdfsUtil == null) {
            hdfsUtil = new HDFSUtil();
            fs = getFileSystem();
            workDir = fs.getWorkingDirectory();
        }
    }

    public static HDFSUtil getInstance() throws Exception {
        if (hdfsUtil == null) syncInit();
        return hdfsUtil;
    }

    /**
     * 配置FileSystem
     */
    private synchronized static FileSystem getFileSystem() throws Exception {
        String ip = ConfigUtil.getProperty("hadoopIP");
        String port = ConfigUtil.getProperty("hadoopPort");
        String hadoopUserName = ConfigUtil.getProperty("hadoopUserName");
        String url = "hdfs://" + ip + ":" + port;
        System.setProperty("HADOOP_USER_NAME", hadoopUserName);
        Configuration config = new Configuration();
        config.set(FileSystem.FS_DEFAULT_NAME_KEY, url);
        return FileSystem.get(config);
    }


    /**
     * 检查文件or路径是否存在
     *
     * @param file 文件or路径
     */
    public boolean checkFileIsExist(String file) throws Exception {
        file = normFileName(file);
        Path path = new Path(workDir + file);
        return fs.exists(path);
    }

    /**
     * 删除所有文件
     */
    public boolean delete() throws Exception {
        Path workDir = fs.getWorkingDirectory();
        return fs.delete(workDir, true);
    }

    public InputStream getHDFSFileStream(String ssid, String file) throws Exception {
        FSDataInputStream FSin = null;
        try {
            file = normFileName(file);
            Path path = new Path(workDir + "/" + ssid + file);
            FSin = fs.open(path);// 获取文件流
            return FSin;
        } finally {
            if (FSin != null) FSin.close();
        }
    }

    /**
     * 下载hdfs文件到本地
     *
     * @param filePath 路径/文件名
     * @param local    本地路径
     */
    public void downloadFile2local(String ssid, String filePath, String local) throws Exception {
        FSDataInputStream FSin = null;
        FileOutputStream out = null;
        try {
            filePath = normFileName(filePath);
            Path path = new Path(workDir + filePath);
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
    public void uploadFile2hdfs(InputStream in, String filePath) throws Exception {
        FSDataOutputStream FSos = null;
        try {
            filePath = normFileName(filePath);
            byte[] content = new byte[in.available()];
            int len = in.read(content);
            Path path = new Path(workDir + filePath);
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
    public void uploadFile2hdfs(String localPath, String hdfsPath) throws Exception {
        InputStream in = new FileInputStream(localPath);
        uploadFile2hdfs(in, hdfsPath);
    }

    /**
     * 创建文件
     */
    public void createFile(String filePath, String fileName, String content) throws Exception {
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
    private String normFileName(String file) {
        file = file.replace('\\', '/');
        if (!file.startsWith("/")) file = "/" + file;
        return file;
    }


}