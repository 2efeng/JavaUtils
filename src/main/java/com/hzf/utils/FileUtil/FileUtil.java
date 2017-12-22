package com.hzf.utils.FileUtil;

import com.hzf.utils.SizeUtil.SizeUtil;
import org.apache.log4j.Logger;

import java.io.*;

class FileUtil {

    private static Logger logger = Logger.getLogger(FileUtil.class.getName());

    /**
     * 写文件
     */
    static void writeFile(String content, String path) throws Exception {
        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file, true);
            fw.append(content);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            if (fw != null)
                fw.close();
        }
        logger.info("write " + path + " success!   fileSize:" + SizeUtil.getStrSize(content, "UTF-8"));
    }


    /**
     * 读文件
     */
    static String readFile(String filePath) throws Exception {
        try {
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
                logger.error("not found file!");
            }
            logger.info("read " + filePath + " success!   fileSize:" + SizeUtil.getStrSize(builder.toString(), encoding));
            return builder.toString();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }
}
