package com.hzf.utils.FTPUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FTPUtil {
    /**
     * 文件
     */
    public static final int TYPE_FILE = 666;
    /**
     * 文件夹
     */
    public static final int TYPE_DIR = 233;
    /**
     * 路径不存在
     */
    public static final int NOT_EXIT = 9487;

    public static final int BINARY = FTP.BINARY_FILE_TYPE;
    public static final int ASCII = FTP.ASCII_FILE_TYPE;
    public static final int EBCDIC = FTP.EBCDIC_FILE_TYPE;
    public static final int LOCAL = FTP.LOCAL_FILE_TYPE;

    /**
     * 连接ftp
     */
    public static FTPClient getFtpClient(FTPConfig config)
            throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(config.getHost(), config.getPort());
        //连接成功后的回应码
        int reply = ftpClient.getReplyCode();
        if (FTPReply.isPositiveCompletion(reply)) {
            if (ftpClient.login(config.getName(), config.getPassword())) {
                ftpClient.setBufferSize(config.getBufferSize());//设置上传缓存大小
                ftpClient.setControlEncoding(config.getEncoding());//设置编码
                ftpClient.setFileType(config.getFileType());
            } else {
                ftpClient.disconnect();
                throw new Exception("FTP server refused login.");
            }
        } else {
            ftpClient.disconnect();
            throw new Exception("FTP server refused connection.");
        }
        return ftpClient;
    }

    /**
     * 重新连接
     */
    public static FTPClient reconnectFTP(FTPClient ftpClient, FTPConfig config) throws Exception {
        disconnectFTP(ftpClient);
        return getFtpClient(config);
    }

    /**
     * 关闭连接
     */
    public static void disconnectFTP(FTPClient ftpClient) throws Exception {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();//退出FTP服务器
            ftpClient.disconnect();//关闭FTP连接
        }
    }

    /**
     * FTP服务器工作目录
     */
    public static String getWorkingDirectory(FTPClient ftpClient) throws Exception {
        return ftpClient.printWorkingDirectory();
    }

    /**
     * cd 工作目录
     *
     * @param dir FTP服务器工作目录
     */
    private static boolean changeDirectory(FTPClient ftpClient, String dir) throws Exception {
        return ftpClient.changeWorkingDirectory(dir);
    }

    /**
     * 在FTP服务器上创建目录
     *
     * @param path 目录
     */
    public static boolean createDirectory(FTPClient ftpClient, String path) throws Exception {
        if (path == null || path.equals("")) throw new Exception("path value is null");
        boolean flag = false;
        String[] dirs = path.split("/");
        StringBuilder rollBackDir = new StringBuilder();
        StringBuilder createDir = new StringBuilder();
        String dir;
        boolean isExit;
        for (String dirStr : dirs) {
            if (dirStr.equals("")) continue;
            createDir.append(dirStr);
            createDir.append("/");
            dir = normFileName(createDir.toString());
            isExit = exitDir(ftpClient, dir);
            if (!isExit) {
                flag = ftpClient.makeDirectory(dir);
                if (!flag && !rollBackDir.toString().equals("")) {
                    deleteDir(ftpClient, normFileName(rollBackDir.toString()));
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
    public static boolean removePath(FTPClient ftpClient, String path) throws Exception {
        int type = checkDir(ftpClient, path);
        switch (type) {
            case TYPE_DIR:
                return deleteDir(ftpClient, path);
            case TYPE_FILE:
                return deleteFile(ftpClient, path);
            case NOT_EXIT:
            default:
                throw new Exception("Del defeat! There are also files under the directory.");
        }
    }

    /**
     * 在FTP服务器上删除目录
     * 如果目录下有文件或者文件夹，则删除失败
     */
    public static boolean removeDir(FTPClient ftpClient, String path) throws Exception {
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
    public static boolean deleteFile(FTPClient ftpClient, String fileName) throws Exception {

        if (ftpClient.deleteFile(fileName)) return true;
        else throw new Exception("del file defeat!");
    }

    /**
     * 删除目录
     */
    public static boolean deleteDir(FTPClient ftpClient, String dir) throws Exception {
        dir = normFileName(dir);
        FTPFile[] ftpFileArr = ftpClient.listFiles(dir);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return ftpClient.removeDirectory(dir);
        }
        for (FTPFile ftpFile : ftpFileArr) {
            String name = dir + "/" + ftpFile.getName();
            if (ftpFile.isDirectory()) {
                deleteDir(ftpClient, name);
            } else if (ftpFile.isFile()) {
                deleteFile(ftpClient, name);
            }
        }
        return removeDir(ftpClient, dir);
    }

    /**
     * 判断文件是否存在
     */
    public static boolean exitFile(FTPClient ftpClient, String fileName) throws Exception {
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
    public static boolean exitDir(FTPClient ftpClient, String path) throws Exception {
        boolean flag = changeDirectory(ftpClient, path);
        if (flag) {
            changeDirectory(ftpClient, "..");
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
    public static int checkDir(FTPClient ftpClient, String path) throws Exception {
        path = normFileName(path);
        boolean isDir = exitDir(ftpClient, path);
        if (isDir) return TYPE_DIR;
        boolean isFile = exitFile(ftpClient, path);
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
    public static List<String> getFileList(FTPClient ftpClient, String path, boolean isShow)
            throws Exception {
        List<String> retList = new ArrayList<>();
        path = normFileName(path);
        getFiles(ftpClient, path, retList, isShow);
        return retList;
    }

    private static void getFiles(FTPClient ftpClient, String path, List<String> retList, boolean isShow)
            throws Exception {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        if (ftpFiles == null || ftpFiles.length == 0) {
            if (isShow) retList.add(path);
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isDirectory()) {
                getFiles(ftpClient, path + "/" + ftpFile.getName(), retList, isShow);
            } else if (ftpFile.isFile()) {
                retList.add(path + "/" + ftpFile.getName());
            }
        }
    }

    /**
     * 上传自定义字符串
     *
     * @param FTPFilePath FTP服务器文件路径
     * @param FTPFileName FTP服务器文件名称
     * @param content     内容
     */
    public static boolean uploadContent(FTPClient ftpClient, String FTPFilePath, String FTPFileName, String content)
            throws Exception {
        InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
        return uploadFile(ftpClient, FTPFilePath, FTPFileName, in);
    }

    /**
     * 上传文件到FTP服务器
     * 在进行上传和下载文件的时候，设置文件的类型最好是：
     * FTPUtil.setFileType(FTPUtil.BINARY_FILE_TYPE)
     *
     * @param FTPFilePath   FTP服务器文件路径
     * @param FTPFileName   FTP服务器文件名称
     * @param localFilePath 本地文件路径和名称
     */
    public static boolean uploadFile(FTPClient ftpClient, String FTPFilePath, String FTPFileName, String localFilePath)
            throws Exception {
        InputStream iStream = new FileInputStream(localFilePath);
        return uploadFile(ftpClient, FTPFilePath, FTPFileName, iStream);
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
    public static boolean uploadFile(FTPClient ftpClient, String FTPFilePath, String FTPFileName, InputStream in) throws Exception {
        boolean flag;
        if (in != null) {
            if (!changeDirectory(ftpClient, FTPFilePath)) createDirectory(ftpClient, FTPFilePath);
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
    public static InputStream getFTPFileStream(FTPClient ftpClient, String FTPFileName) throws Exception {
        FTPFileName = normFileName(FTPFileName);
        if (!exitFile(ftpClient, FTPFileName)) throw new Exception("File " + FTPFileName + " does not exit.");
        return ftpClient.retrieveFileStream(FTPFileName);
    }

    /**
     * 下载文件到本地
     *
     * @param FTPFileName   FTP服务器资源文件名称
     * @param localFileName 本地目录
     */
    public static boolean downloadFTPFile2local(FTPClient ftpClient, String FTPFileName, String localFileName) throws Exception {
        FileOutputStream out = null;
        if (!exitFile(ftpClient, normFileName(FTPFileName)))
            throw new Exception("File " + FTPFileName + " does not exit.");
        boolean flag;
        try {
            out = new FileOutputStream(localFileName);
            flag = ftpClient.retrieveFile(normFileName(FTPFileName), out);
        } finally {
            if (out != null) out.close();
        }
        return flag;
    }

    /**
     * 获取文件内容
     * <p>
     * docx 其实是zip 需要用poi读取
     */
    public static String getFileContent(FTPClient ftpClient, String path) throws Exception {
        InputStream in = null;
        OPCPackage opcPackage = null;
        POIXMLTextExtractor extractor = null;
        WordExtractor ex = null;
        InputStreamReader read = null;
        BufferedReader reader = null;
        StringBuilder s = new StringBuilder();
        try {
            in = getFTPFileStream(ftpClient, path);
            if (path.endsWith(".doc") || path.endsWith(".DOC")) {
                ex = new WordExtractor(in);
                s.append(ex.getText());
            } else if (path.endsWith("docx") || path.endsWith(".DOCX")) {
                opcPackage = OPCPackage.open(in);
                extractor = new XWPFWordExtractor(opcPackage);
                s.append(extractor.getText());
            } else if (path.endsWith(".pdf") || path.endsWith(".PDF")) {

            } else {
                String line;
                read = new InputStreamReader(in, "GBK");
                reader = new BufferedReader(read);
                while ((line = reader.readLine()) != null) {
                    s.append(line).append("\n");
                }
            }
        } finally {
            if (ex != null) ex.close();
            if (extractor != null) extractor.close();
            if (opcPackage != null) opcPackage.close();
            if (reader != null) reader.close();
            if (read != null) read.close();
            if (in != null) in.close();
        }
        return s.toString();
    }

    //规范文件名
    private static String normFileName(String file) throws Exception {
        if (file.equals("") || file.equals("/")) return "";
        file = file.replace('\\', '/');
        if (!file.startsWith("/")) file = "/" + file;
        return setEncodedGBK2ISO_8859_1(file);
    }

    //设置编码格式 ，使FTP能够正确识别中文
    //GBK -> ISO-8859-1
    private static String setEncodedGBK2ISO_8859_1(String str) throws Exception {
        return new String(str.getBytes("GBK"), FTP.DEFAULT_CONTROL_ENCODING);
    }

}
