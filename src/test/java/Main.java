import com.hzf.utils.ConfigUtil.ConfigUtil;
import com.hzf.utils.FTPUtil.FTPUtil;
import org.apache.commons.net.ftp.FTPClient;

public class Main {

    public static void main(String[] args) throws Exception {

        FTPClient ftpClient = getFtpClient();
        boolean result = FTPUtil.uploadContent(ftpClient, "uploadContent", "test2", "test content 哈哈哈哈");
        System.out.println(result);
        String content = FTPUtil.getFileContent(ftpClient, "uploadContent/test2");
        System.out.println(content);

    }

    private static FTPClient getFtpClient() throws Exception {
        String host = ConfigUtil.getProperty("FtpServer");
        int port = Integer.valueOf(ConfigUtil.getProperty("FtpPort"));
        String name = ConfigUtil.getProperty("FtpName");
        String pwd = ConfigUtil.getProperty("FtpPwd");
        return FTPUtil.getFtpClient(host, port, name, pwd);
    }
}
