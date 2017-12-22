package com.hzf.utils.Base64Util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.InputStream;

public class Base64Util {

    /**
     * 将Base64位编码的文件进行解码
     */
    public static byte[] decodeBase64ToFile(String base64) throws Exception {
        if (base64 == null || base64.equals(""))
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64);
    }

    /**
     * 文件→base64
     */
    public static String file2base64(InputStream in) throws Exception {
        byte[] data = new byte[in.available()];
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }


}
