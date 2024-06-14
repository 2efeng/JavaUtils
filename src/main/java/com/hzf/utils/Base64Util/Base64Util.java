//package com.hzf.utils.Base64Util;
//
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
//public class Base64Util {
//
//
//
//    /**
//     * 将Base64位编码的文件进行解码
//     */
//    public static byte[] decodeBase64ToByte(String base64) throws Exception {
//        if (base64 == null || base64.equals(""))
//            return null;
//        BASE64Decoder decoder = new BASE64Decoder();
//        return decoder.decodeBuffer(base64);
//    }
//
//    public static boolean decodeBase64ToFile(String base64, String localPath) throws Exception {
//        FileOutputStream out = new FileOutputStream(localPath);
//        return decodeBase64ToFile(base64, out);
//    }
//
//    public static boolean decodeBase64ToFile(String base64, FileOutputStream out) throws Exception {
//        if (out == null) throw new Exception("file is null!");
//        byte[] contentByte = decodeBase64ToByte(base64);
//        try {
//            out.write(contentByte);
//        } finally {
//            out.close();
//        }
//        return true;
//    }
//
//
//    /**
//     * 文件→base64
//     */
//    public static String file2base64(String localPath) throws Exception {
//        FileInputStream in = new FileInputStream(localPath);
//        return file2base64(in);
//    }
//
//
//    public static String file2base64(InputStream in) throws Exception {
//        byte[] data = new byte[in.available()];
//        BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(data);
//    }
//
//
//}
