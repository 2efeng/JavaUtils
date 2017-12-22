package com.hzf.utils.FTPUtil;

import com.hzf.utils.ConfigUtil.ConfigUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.net.ftp.FTPReply.UNRECOGNIZED_COMMAND;

public class FTPUtil {

    private static volatile FTPClient ftpClient;
    private static volatile FTPUtil FTPUtil;

    private static final int TYPE_FILE = 666;
    private static final int TYPE_DIR = 233;
    private static final int NOT_EXIT = -10086;

    private FTPUtil() {
    }

    private static synchronized void syncInit() throws Exception {
        if (FTPUtil == null) {
            FTPUtil = new FTPUtil();
            connectFTP();
        }
    }

    public static FTPUtil getInstance() throws Exception {
        if (FTPUtil == null) {
            syncInit();
        }
        return FTPUtil;
    }

    private static void connectFTP() throws Exception {
        String host = ConfigUtil.getProperty("FtpServer");
        int port = Integer.valueOf(ConfigUtil.getProperty("FtpPort"));
        String name = ConfigUtil.getProperty("FtpName");
        String pwd = ConfigUtil.getProperty("FtpPwd");
        connectServer(host, port, name, pwd);
    }


    /**
     * 使用详细信息进行服务器连接
     *
     * @param server   服务器地址名称
     * @param port     端口号
     * @param user     用户名
     * @param password 用户密码
     */
    private static void connectServer(String server, int port, String user, String password)
            throws Exception {
        if (ftpClient == null) ftpClient = new FTPClient();
        if (ftpClient.isConnected()) return;
        ftpClient.connect(server, port);
        //连接成功后的回应码
        int reply = ftpClient.getReplyCode();
        try {
            if (FTPReply.isPositiveCompletion(reply)) {
                if (ftpClient.login(user, password)) {
                    ftpClient.setBufferSize(1024);//设置上传缓存大小
                    ftpClient.setControlEncoding("GBK");//设置编码
                    setFileType(FTPClient.BINARY_FILE_TYPE);

                } else {
                    ftpClient.disconnect();
                    throw new Exception("FTP server refused login.");
                }
            } else {
                ftpClient.disconnect();
                throw new Exception("FTP server refused connection.");
            }
        } catch (Exception e) {
            if (ftpClient.isConnected()) ftpClient.disconnect();
            throw e;
        }
    }

    /**
     * FTP.BINARY_FILE_TYPE 二进制文件
     * FTP.ASCII_FILE_TYPE 文本文件
     *
     * @param fileType 文件类型
     */
    private static void setFileType(int fileType) throws Exception {
        ftpClient.setFileType(fileType);
    }

    /**
     * 关闭连接
     */
    private static void closeFTP() throws Exception {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();//退出FTP服务器
            ftpClient.disconnect();//关闭FTP连接
        }
    }

    /**
     * FTP服务器工作目录
     */
    private String getWorkingDirectory() throws Exception {
        return ftpClient.printWorkingDirectory();
    }

    /**
     * cd 工作目录
     *
     * @param dir FTP服务器工作目录
     */
    private boolean changeDirectory(String dir) throws Exception {
        return ftpClient.changeWorkingDirectory(dir);
    }

    /**
     * 在FTP服务器上创建目录
     *
     * @param path 目录
     */
    private boolean createDirectory(String path) throws Exception {
        if (path == null || path.equals("")) throw new Exception("path is null");
        boolean flag = false;
        String[] dirs = path.split("/");
        StringBuilder rollBackDir = new StringBuilder();
        StringBuilder createDir = new StringBuilder();
        String dir;
        boolean isExit;
        for (String dirStr : dirs) {
            createDir.append(dirStr);
            createDir.append("/");
            dir = normFileName(createDir.toString());
            isExit = exitDir(dir);
            if (!isExit) {
                flag = ftpClient.makeDirectory(dir);
                if (!flag && !rollBackDir.toString().equals("")) {
                    deleteDir(normFileName(rollBackDir.toString()));
                    flag = false;
                    break;
                }
                rollBackDir.append(dir);
            }
        }
        return flag;
    }

    /**
     * 在FTP服务器上删除目录
     * 如果目录下有文件，则删除失败
     *
     * @param path 目录
     */
    public boolean removeDirectory(String path) throws Exception {
        return ftpClient.removeDirectory(path);
    }

    /**
     * 删除FTP服务器上的文件
     *
     * @param fileName FTP服务器文件名称
     */
    private boolean removeFile(String fileName) throws Exception {
        return ftpClient.deleteFile(fileName);
    }

    /**
     * 删除所有文件
     *
     * @param dir 目录
     */
    public boolean deleteDir(String dir) throws Exception {
        dir = normFileName(dir);
        FTPFile[] ftpFileArr = ftpClient.listFiles(dir);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return removeDirectory(dir);
        }
        for (FTPFile ftpFile : ftpFileArr) {
            String name = dir + "/" + ftpFile.getName();
            if (ftpFile.isDirectory()) {
                removeDirectory(name);
            } else if (ftpFile.isFile()) {
                removeFile(name);
            }
        }
        return removeDirectory(dir);
    }


    /**
     * 判断文件是否存在
     *
     * @param fileName 文件名
     */
    private boolean exitFile(String fileName) throws Exception {
        InputStream in = ftpClient.retrieveFileStream(fileName);
        if (in == null || ftpClient.getReplyCode() == UNRECOGNIZED_COMMAND)
            throw new Exception(fileName + " does not exit.");
        in.close();
        // 必须执行，否则在循环检查多个文件时会出错
        ftpClient.completePendingCommand();
        return true;
    }

    /**
     * 判断目录是否存在
     *
     * @param path 目录
     */
    private boolean exitDir(String path) throws Exception {
        boolean flag = changeDirectory(path);
        if (flag) {
            changeDirectory("..");
            return true;
        }
        return false;
    }

    /**
     * 判断是文件还是文件夹
     * TYPE_DIR  233 ：文件夹
     * TYPE_FILE 666 ：文件
     *
     * @param path 目录
     */
    public int checkDir(String path) throws Exception {
        path = normFileName(path);
        boolean isDir = exitDir(path);
        if (isDir) return TYPE_DIR;
        boolean isFile = exitFile(path);
        if (isFile) return TYPE_FILE;
        else return NOT_EXIT;
    }


    /**
     * 得到文件列表,listFiles返回包含目录和文件，它返回的是一个FTPFile数组
     * listNames()：只包含目录的字符串数组
     * String[] fileNameArr = ftpClient.listNames(path);
     * <p>
     * FTPFile[] FileList = ftpClient.listFiles(path, FTPFile::isFile);
     * FTPFile[] DirectoryList = ftpClient.listFiles(path, FTPFile::isDirectory);
     *
     * @param path FTP服务器上的文件目录
     */
    List<String> getFileList(String path) throws Exception {
        List<String> retList = new ArrayList<>();
        getFiles(path, retList);
        return retList;
    }

    private void getFiles(String path, List<String> retList) throws Exception {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        if (ftpFiles == null || ftpFiles.length == 0) return;
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isDirectory()) {
                getFiles(path + "/" + ftpFile.getName(), retList);
            } else if (ftpFile.isFile()) {
                retList.add(path + "/" + ftpFile.getName());
            }
        }
    }


    /**
     * 上传文件到FTP服务器
     * 在进行上传和下载文件的时候，设置文件的类型最好是：
     * FTPUtil.setFileType(FTPUtil.BINARY_FILE_TYPE)
     *
     * @param FTPFileName   FTP服务器文件名称
     * @param localFilePath 本地文件路径和名称
     */
    public boolean uploadFile(String FTPFilePath, String FTPFileName, String localFilePath) throws Exception {
        InputStream iStream = new FileInputStream(localFilePath);
        return uploadFile(FTPFilePath, FTPFileName, iStream);
    }

    /**
     * 上传文件到FTP服务器
     * <p>
     * 也可以使用BufferedInputStream进行封装
     * BufferedInputStream bis=new BufferedInputStream(iStream);
     * flag = ftpClient.storeFile(remoteFileName, bis);
     *
     * @param FTPFilePath FTP服务器文件路径
     * @param FTPFileName FTP服务器文件名称
     * @param in          本地文件输入流
     */
    boolean uploadFile(String FTPFilePath, String FTPFileName, InputStream in) throws Exception {
        boolean flag;
        if (in != null) {
            if (!changeDirectory(FTPFilePath)) createDirectory(FTPFilePath);
            flag = ftpClient.storeFile(normFileName(FTPFilePath + "/" + FTPFileName), in);
        } else {
            throw new Exception("InputStream is null");
        }
        in.close();
        return flag;
    }


    /**
     * 获取FTP服务器上的文件流
     *
     * @param FTPFileName FTP服务器资源文件名称
     */
    InputStream getFTPFileStream(String FTPFileName) throws Exception {
        InputStream in = null;
        //TODO 判断FTP文件是否存在
        try {
            in = ftpClient.retrieveFileStream(normFileName(FTPFileName));
            return in;
        } finally {
            if (in != null) in.close();
        }
    }

    /**
     * 下载文件到本地
     *
     * @param FTPFileName   FTP服务器资源文件名称
     * @param localFileName 本地目录
     */
    boolean downloadFTPFile2local(String FTPFileName, String localFileName) throws Exception {
        FileOutputStream out = null;
        //TODO 判断FTP文件是否存在
        boolean flag;
        try {
            out = new FileOutputStream(localFileName);
            flag = ftpClient.retrieveFile(normFileName(FTPFileName), out);
        } finally {
            if (out != null) out.close();
        }
        return flag;
    }

    //规范文件名
    private String normFileName(String file) throws Exception {
        file = file.replace('\\', '/');
        if (!file.startsWith("/")) file = "/" + file;
        return setEncodedGBK2ISO_8859_1(file);
    }

    //设置编码格式 ，使FTP能够正确识别中文
    //GBK -> ISO-8859-1
    private String setEncodedGBK2ISO_8859_1(String str) throws Exception {
        return new String(str.getBytes("GBK"), FTP.DEFAULT_CONTROL_ENCODING);
    }

    //识别FTP上的中文
    //ISO-8859-1 -> GBK
    private String setEncodedISO_8859_12GBK(String str) throws Exception {
        return new String(str.getBytes(FTP.DEFAULT_CONTROL_ENCODING), "GBK");
    }

}
