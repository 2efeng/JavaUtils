package com.hzf.utils.HDFSUtil;

import com.hzf.utils.ConfigUtil.ConfigUtil;
import com.hzf.utils.DateUtil.DateUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

class HDFSUtil {

    private static volatile HDFSUtil hdfsUtil;

    private HDFSUtil() {
    }

    private static synchronized void syncInit() {
        if (hdfsUtil == null) {
            hdfsUtil = new HDFSUtil();
        }
    }

    static HDFSUtil getInstance() {
        if (hdfsUtil == null) {
            syncInit();
        }
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
        config.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        config.set("fs.defaultFS", url);
        return FileSystem.get(config);
    }


    /**
     * 检查文件or路径是否存在
     *
     * @param file 文件or路径
     */
    boolean checkFileIsExist(String file) throws Exception {
        FileSystem fs = getFileSystem();
        file = normFileName(file);
        Path workDir = fs.getWorkingDirectory();
        Path path = new Path(workDir + file);
        return fs.exists(path);
    }

    /**
     * 删除所有文件
     */
    boolean delete() throws Exception {
        FileSystem fs = getFileSystem();
        Path workDir = fs.getWorkingDirectory();
        return fs.delete(workDir, true);
    }

    InputStream getHDFSFileStream(String ssid, String file) throws Exception {
        FileSystem fs = null;
        FSDataInputStream FSin = null;
        try {
            fs = getFileSystem();
            file = normFileName(file);
            Path workDir = fs.getWorkingDirectory();
            Path path = new Path(workDir + "/" + ssid + file);
            FSin = fs.open(path);// 获取文件流
            return FSin;
        } finally {
            if (FSin != null) FSin.close();
            if (fs != null) fs.close();
        }
    }

    /**
     * 下载hdfs文件到本地
     *
     * @param ssid  spiderID
     * @param file  路径/文件名
     * @param local 本地路径
     */
    void downloadFile2local(String ssid, String file, String local) throws Exception {
        FileSystem fs = null;
        FSDataInputStream FSin = null;
        FileOutputStream out = null;
        try {
            fs = getFileSystem();
            file = normFileName(file);
            Path workDir = fs.getWorkingDirectory();
            Path path = new Path(workDir + "/" + ssid + file);
            FSin = fs.open(path);// 获取文件流
            byte[] data = new byte[FSin.available()];
            out = new FileOutputStream(local);
            out.write(data);
        } finally {
            if (out != null) out.close();
            if (FSin != null) FSin.close();
            if (fs != null) fs.close();
        }
    }

    /**
     * 上传文件到hdfs
     *
     * @param ssid String spiderId
     * @param file String hdfs 路径/文件名
     * @param in   FileInputStream 文件流
     */
    void uploadFile2hdfs(String ssid, String file, InputStream in) throws Exception {
        FileSystem fs = null;
        FSDataOutputStream FSos = null;
        try {
            fs = getFileSystem();
            file = normFileName(file);
            byte[] content = new byte[in.available()];
            int len = in.read(content);
            Path path = new Path(fs.getWorkingDirectory() + "/" + ssid + file);
            long start = DateUtil.currentTimeStamp();
            FSos = fs.create(path);
            FSos.write(content, 0, len);
            long end = DateUtil.currentTimeStamp();
            System.out.println(end - start);

        } finally {
            if (FSos != null) FSos.close();
            if (fs != null) fs.close();
            if (in != null) in.close();
        }
    }


    /**
     * 判断文件、目录是否存在
     */
    boolean isExit(String path) {
        return false;
    }

    /**
     * 规范文件名
     */
    private String normFileName(String file) {
        file = file.replace('\\', '/');
        if (!file.startsWith("/")) {
            file = "/" + file;
        }
        return file;
    }


}