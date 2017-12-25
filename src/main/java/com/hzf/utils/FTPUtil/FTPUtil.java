package com.hzf.utils.FTPUtil;

import com.hzf.utils.ConfigUtil.ConfigUtil;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FTPUtil {

    /**
     * 文件
     */
    private static final int TYPE_FILE = 666;
    /**
     * 文件夹
     */
    private static final int TYPE_DIR = 233;
    /**
     * 路径不存在
     */
    private static final int NOT_EXIT = -10086;

    private static volatile FTPClient ftpClient;
    private static volatile FTPUtil FTPUtil;

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
     * 关闭连接
     */
    public static void disconnectFTP() throws Exception {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();//退出FTP服务器
            ftpClient.disconnect();//关闭FTP连接
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
    public boolean createDirectory(String path) throws Exception {
        if (path == null || path.equals("")) throw new Exception("path value is null");
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
     * 删除文件/文件夹
     */
    public boolean removePath(String path) throws Exception {
        int type = checkDir(path);
        switch (type) {
            case TYPE_DIR:
                return deleteDir(path);
            case TYPE_FILE:
                return deleteFile(path);
            case NOT_EXIT:
            default:
                throw new Exception("Del defeat! There are also files under the directory.");
        }
    }

    /**
     * 在FTP服务器上删除目录
     * 如果目录下有文件或者文件夹，则删除失败
     */
    public boolean removeDir(String path) throws Exception {
        path = normFileName(path);
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return ftpClient.removeDirectory(path);
        } else {
            throw new Exception("Del dir defeat! There are also files under the directory.");
        }
    }


    /**
     * 删除FTP服务器上的文件
     */
    public boolean deleteFile(String fileName) throws Exception {

        if (ftpClient.deleteFile(fileName)) return true;
        else throw new Exception("del file defeat!");
    }

    /**
     * 删除目录
     */
    public boolean deleteDir(String dir) throws Exception {
        dir = normFileName(dir);
        FTPFile[] ftpFileArr = ftpClient.listFiles(dir);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return ftpClient.removeDirectory(dir);
        }
        for (FTPFile ftpFile : ftpFileArr) {
            String name = dir + "/" + ftpFile.getName();
            if (ftpFile.isDirectory()) {
                deleteDir(name);
            } else if (ftpFile.isFile()) {
                deleteFile(name);
            }
        }
        return removeDir(dir);
    }


    /**
     * 判断文件是否存在
     */
    private boolean exitFile(String fileName) throws Exception {
        InputStream in = ftpClient.retrieveFileStream(fileName);
        if (in == null || ftpClient.getReplyCode() == FTPReply.UNRECOGNIZED_COMMAND)
            return false;
        in.close();
        // 必须执行，否则在循环检查多个文件时会出错
        ftpClient.completePendingCommand();
        return true;
    }

    /**
     * 判断目录是否存在
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
     * NOT_EXIT      ：路径不存在
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
     * @param path   FTP服务器上的文件目录
     * @param isShow 是否显示空目录
     */
    List<String> getFileList(String path, boolean isShow) throws Exception {
        List<String> retList = new ArrayList<>();
        path = normFileName(path);
        getFiles(path, retList, isShow);
        return retList;
    }

    private void getFiles(String path, List<String> retList, boolean isShow) throws Exception {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        if (ftpFiles == null || ftpFiles.length == 0) {
            if (isShow) retList.add(path);
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isDirectory()) {
                getFiles(path + "/" + ftpFile.getName(), retList, isShow);
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
    public boolean uploadFile(String FTPFilePath, String FTPFileName, InputStream in) throws Exception {
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
    public InputStream getFTPFileStream(String FTPFileName) throws Exception {
        FTPFileName = normFileName(FTPFileName);
        if (!exitFile(FTPFileName)) throw new Exception("File " + FTPFileName + " does not exit.");
        return ftpClient.retrieveFileStream(FTPFileName);
    }

    /**
     * 下载文件到本地
     *
     * @param FTPFileName   FTP服务器资源文件名称
     * @param localFileName 本地目录
     */
    public boolean downloadFTPFile2local(String FTPFileName, String localFileName) throws Exception {
        FileOutputStream out = null;
        FTPFileName = normFileName(FTPFileName);
        if (!exitFile(FTPFileName)) throw new Exception(FTPFileName + " does not exit.");
        boolean flag;
        try {
            out = new FileOutputStream(localFileName);
            flag = ftpClient.retrieveFile(normFileName(FTPFileName), out);
        } finally {
            if (out != null) out.close();
        }
        return flag;
    }

    public String getFileContent(String path) throws Exception {
        InputStream in = getFTPFileStream(path);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int i;
//        while ((i = in.read()) != -1) {
//            baos.write(i);
//        }
//        String content = baos.toString();
//        baos.close();
//        in.close();
//        return content;


        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            in.close();
        }
        return new String(sb.toString().getBytes("GB2312"), "utf-8");
    }


    //规范文件名
    private String normFileName(String file) throws Exception {
        if (file.equals("") || file.equals("/")) return "";
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
    private String setEncodedISO_8859_1ToGBK(String str) throws Exception {
        return new String(str.getBytes(FTP.DEFAULT_CONTROL_ENCODING), "GBK");
    }

    private String setEncoded(String str, String encoded) throws Exception {
        return new String(str.getBytes(FTP.DEFAULT_CONTROL_ENCODING), encoded);
    }

    public static void main(String[] args) throws Exception {
        List<String> list = getInstance().getFileList("/", false);
        for (String str : list)
            System.out.println(str);
//        System.out.println(getInstance().getFileContent("HBaseClient接口文档.docx"));
    }

}
