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
//        String pattern = "yyyy-MM-dd HH:mm:ss SSS";
//        Date date = DateUtil.getDate(2018, 1, 10, 12, 0, 0, 0);
//        System.out.println(date.getMonth());

//        Date date = calendar.getTime();
//        System.out.println(format.format(date));


        long start = System.nanoTime();
        Date startDate = new Date();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long period = System.nanoTime() - start;
        System.out.println(String.format("period(nanoseconds): %d, period(seconds): %d", period, TimeUnit.NANOSECONDS.toSeconds(period)));
        long num = TimeUnit.NANOSECONDS.toMillis(period);
        long end = System.currentTimeMillis();
        Date endDate = new Date(end - num);
        System.out.println(startDate);
        System.out.println(endDate);


//        Date date = new Date();
//        long nano = System.nanoTime();
//        Date dateNano = new Date(nano);
//        System.out.println(date);
//        System.out.println(dateNano);
//        NanoSec();
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

    private static void test(int... a) {
        System.out.println(a instanceof int[]);
//        for (int b : a) {
//            System.out.print(b + " ");
//        }
//        System.out.println();
    }
}
