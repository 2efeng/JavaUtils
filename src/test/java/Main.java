import com.hzf.utils.ConfigUtil.ConfigUtil;
import com.hzf.utils.DateUtil.DateUtil;
import com.hzf.utils.FTPUtil.FTPConfig;
import com.hzf.utils.FTPUtil.FTPUtil;
import com.hzf.utils.FileUtil.FileUtil;
import com.hzf.utils.Word2html.Word2html;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        FTPClient client = getFtpClient();
    }


    private static void test16num() {
        int a = 0xABCD;
        System.out.println(a);
        System.out.format("%x ", a);
    }

    private static void test(boolean a, boolean b, boolean c) {
        //异或
        //同 => false 0
        //异 => true 1
        System.out.print("a:" + String.valueOf(a) + "\tb:" + String.valueOf(b) + "\tc:" + String.valueOf(c));
        System.out.print("\t\t");
        System.out.print("(a^b) => " + (a ^ b));
        System.out.print("\t,\t");
        System.out.println("(a^b ? c:a) => " + (a ^ b ? c : a));
    }

    public static void NanoSec() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        long startNano = System.nanoTime();
        long startMill = System.currentTimeMillis();
        for (int i = 1; i <= 10; i++) {
            Thread.sleep(1000L);
            long nano = System.nanoTime();
            System.out.print(sdf.format(new java.util.Date(nano)) + "<=>");
            System.out.print(sdf.format(new java.util.Date(nano / (1000))) + "<-->");
            if (i % 2 == 0) {
                System.out.println();
            }
        }
        System.out.println("nano:" + (System.nanoTime() - startNano));
        System.out.println("mill:" + (System.currentTimeMillis() - startMill));

        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());
        System.out.println(Long.MAX_VALUE);
        System.out.println(Long.MIN_VALUE);
    }

    public static String readWord(String path) {
        String s = "";
        try {
            if (path.endsWith(".doc")) {
                InputStream is = new FileInputStream(new File(path));
                WordExtractor ex = new WordExtractor(is);
                s = ex.getText();
            } else if (path.endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(path);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                s = extractor.getText();
            } else {
                System.out.println("传入的word文件不正确:" + path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    private static FTPClient getFtpClient() throws Exception {
        String host = ConfigUtil.getProperty("FtpServer");
        int port = Integer.valueOf(ConfigUtil.getProperty("FtpPort"));
        String name = ConfigUtil.getProperty("FtpName");
        String pwd = ConfigUtil.getProperty("FtpPwd");

        FTPConfig config = new FTPConfig.Builder(host, port, name, pwd)
                .setBufferSize(1024)
                .setEncoding("GBK")
                .setFileType(FTPUtil.BINARY)
                .build();

        return FTPUtil.getFtpClient(config);
    }

}
