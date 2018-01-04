import com.hzf.utils.ConfigUtil.ConfigUtil;
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

public class Main {

    public static void main(String[] args) throws Exception {

        String str = "D:\\files\\Hadoop安装.docx";

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
