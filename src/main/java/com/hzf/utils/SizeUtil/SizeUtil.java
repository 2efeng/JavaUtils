package com.hzf.utils.SizeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * 获取文件、流、字符串的大小
 */
public class SizeUtil {

    //按encode编码来编码
    public static String getStrSize(String content, String encode) throws Exception {
        return getResult(content.getBytes(encode).length);
    }

    //获取文件大小
    public static String getFileSize(String path) throws Exception {
        File file = new File(path);
        return getFileSize(file);
    }

    public static String getFileSize(File file) throws Exception {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return getFileSize(in);
        } finally {
            assert in != null;
            in.close();
        }
    }

    public static String getFileSize(FileInputStream in) throws Exception {
        FileChannel fc = in.getChannel();
        return getResult(fc.size());
    }

    private static String getResult(long size) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String result;
        if (size < 1024) {
            result = String.valueOf(size) + "B";
        } else if (size < 1024 * 1024) {
            result = String.valueOf(df.format(size / (double) 1024)) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            result = String.valueOf(df.format(size / (double) (1024 * 1024))) + "MB";
        } else {
            result = String.valueOf(df.format(size / (double) (1024 * 1024 * 1024))) + "GB";
        }
        return result;
    }

}
