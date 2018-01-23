package com.hzf.utils.ConfigUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static Logger logger = Logger.getLogger(ConfigUtil.class.getName());
    private static Properties props;

    static {
        loadConfigByResource();

    }

    //加载config
    private synchronized static void loadConfigByResource() {
        try {
            ClassLoader loader = ConfigUtil.class.getClassLoader();
            InputStream in = loader.getResourceAsStream("config.properties");
            props = new Properties();
            props.load(in);
            in.close();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    //加载config
    private synchronized static void loadConfigByPath() {
        String proFilePath = System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "resources"
                + File.separator + "config.properties";

        try {
            InputStream in = new FileInputStream(proFilePath);
            props = new Properties();
            props.load(in);
            in.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }


    //加载log4j
    private synchronized static void loadLog4jByPath() {
        String logFilePath = System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "resources"
                + File.separator + "log4j.properties";

        PropertyConfigurator.configure(logFilePath);
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}
