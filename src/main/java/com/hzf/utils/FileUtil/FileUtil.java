package com.hzf.utils.FileUtil;

import java.io.*;

public class FileUtil {

    /**
     * 写文件
     */
    public static void writeFile(String content, String path) throws Exception {
        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file, true);
            fw.append(content);
        } finally {
            if (fw != null) fw.close();
        }
    }


    /**
     * 读文件
     */
    public static String readFile(String filePath) throws Exception {
        String encoding = "UTF-8";
        File file = new File(filePath);
        StringBuilder builder = new StringBuilder();
        if (file.isFile() && file.exists()) {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                builder.append(lineTxt);
            }
            read.close();
        } else {
            throw new Exception("not found file!");
        }
        return builder.toString();
    }
}
