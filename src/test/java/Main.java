/*
                       ::
                      :;J7, :,                        ::;7:
                      ,ivYi, ,                       ;LLLFS:
                      :iv7Yi                       :7ri;j5PL
                     ,:ivYLvr                    ,ivrrirrY2X,
                     :;r@Wwz.7r:                :ivu@kexianli.
                    :iL7::,:::iiirii:ii;::::,,irvF7rvvLujL7ur
                   ri::,:,::i:iiiiiii:i:irrv177JX7rYXqZEkvv17
                ;i:, , ::::iirrririi:i:::iiir2XXvii;L8OGJr71i
              :,, ,,:   ,::ir@mingyi.irii:i:::j1jri7ZBOS7ivv,
                 ,::,    ::rv77iiiriii:iii:i::,rvLq@huhao.Li
             ,,      ,, ,:ir7ir::,:::i;ir:::i:i::rSGGYri712:
           :::  ,v7r:: ::rrv77:, ,, ,:i7rrii:::::, ir7ri7Lri
          ,     2OBBOi,iiir;r::        ,irriiii::,, ,iv7Luur:
        ,,     i78MBBi,:,:::,:,  :7FSL: ,iriii:::i::,,:rLqXv::
        :      iuMMP: :,:::,:ii;2GY7OBB0viiii:i:iii:i:::iJqL;::
       ,     ::::i   ,,,,, ::LuBBu BBBBBErii:i:i:i:i:i:i:r77ii
      ,       :       , ,,:::rruBZ1MBBqi, :,,,:::,::::::iiriri:
     ,               ,,,,::::i:  @arqiao.       ,:,, ,:::ii;i7:
    :,       rjujLYLi   ,,:::::,:::::::::,,   ,:i,:,,,,,::i:iii
    ::      BBBBBBBBB0,    ,,::: , ,:::::: ,      ,,,, ,,:::::::
    i,  ,  ,8BMMBBBBBBi     ,,:,,     ,,, , ,   , , , :,::ii::i::
    :      iZMOMOMBBM2::::::::::,,,,     ,,,,,,:,,,::::i:irr:i:::,
    i   ,,:;u0MBMOG1L:::i::::::  ,,,::,   ,,, ::::::i:i:iirii:i:i:
    :    ,iuUuuXUkFu7i:iii:i:::, :,:,: ::::::::i:i:::::iirr7iiri::
    :     :rk@Yizero.i:::::, ,:ii:::::::i:::::i::,::::iirrriiiri::,
     :      5BMBBBBBBSr:,::rv2kuii:::iii::,:i:,, , ,,:,:i@petermu.,
          , :r50EZ8MBBBBGOBBBZP7::::i::,:::::,: :,:,::i;rrririiii::
              :jujYY7LS0ujJL7r::,::i::,::::::::::::::iirirrrrrrr:ii:
           ,:  :@kevensun.:,:,,,::::i:i:::::,,::::::iir;ii;7v77;ii;i,
           ,,,     ,,:,::::::i:iiiii:i::::,, ::::iiiir@xingjief.r;7:i,
        , , ,,,:,,::::::::iiiiiiiiii:,:,:::::::::iiir;ri7vL77rrirri::
         :,, , ::::::::i:::i:::i:i::,,,,,:,::i:i:::iir;@Secbone.ii:::

*/

import com.hzf.utils.ConfigUtil.ConfigUtil;
import com.hzf.utils.FTPUtil.FTPConfig;
import com.hzf.utils.FTPUtil.FTPUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("                       ::\n" +
                "                      :;J7, :,                        ::;7:\n" +
                "                      ,ivYi, ,                       ;LLLFS:\n" +
                "                      :iv7Yi                       :7ri;j5PL\n" +
                "                     ,:ivYLvr                    ,ivrrirrY2X,\n" +
                "                     :;r@Wwz.7r:                :ivu@kexianli.\n" +
                "                    :iL7::,:::iiirii:ii;::::,,irvF7rvvLujL7ur\n" +
                "                   ri::,:,::i:iiiiiii:i:irrv177JX7rYXqZEkvv17\n" +
                "                ;i:, , ::::iirrririi:i:::iiir2XXvii;L8OGJr71i\n" +
                "              :,, ,,:   ,::ir@mingyi.irii:i:::j1jri7ZBOS7ivv,\n" +
                "                 ,::,    ::rv77iiiriii:iii:i::,rvLq@huhao.Li\n" +
                "             ,,      ,, ,:ir7ir::,:::i;ir:::i:i::rSGGYri712:\n" +
                "           :::  ,v7r:: ::rrv77:, ,, ,:i7rrii:::::, ir7ri7Lri\n" +
                "          ,     2OBBOi,iiir;r::        ,irriiii::,, ,iv7Luur:\n" +
                "        ,,     i78MBBi,:,:::,:,  :7FSL: ,iriii:::i::,,:rLqXv::\n" +
                "        :      iuMMP: :,:::,:ii;2GY7OBB0viiii:i:iii:i:::iJqL;::\n" +
                "       ,     ::::i   ,,,,, ::LuBBu BBBBBErii:i:i:i:i:i:i:r77ii\n" +
                "      ,       :       , ,,:::rruBZ1MBBqi, :,,,:::,::::::iiriri:\n" +
                "     ,               ,,,,::::i:  @arqiao.       ,:,, ,:::ii;i7:\n" +
                "    :,       rjujLYLi   ,,:::::,:::::::::,,   ,:i,:,,,,,::i:iii\n" +
                "    ::      BBBBBBBBB0,    ,,::: , ,:::::: ,      ,,,, ,,:::::::\n" +
                "    i,  ,  ,8BMMBBBBBBi     ,,:,,     ,,, , ,   , , , :,::ii::i::\n" +
                "    :      iZMOMOMBBM2::::::::::,,,,     ,,,,,,:,,,::::i:irr:i:::,\n" +
                "    i   ,,:;u0MBMOG1L:::i::::::  ,,,::,   ,,, ::::::i:i:iirii:i:i:\n" +
                "    :    ,iuUuuXUkFu7i:iii:i:::, :,:,: ::::::::i:i:::::iirr7iiri::\n" +
                "    :     :rk@Yizero.i:::::, ,:ii:::::::i:::::i::,::::iirrriiiri::,\n" +
                "     :      5BMBBBBBBSr:,::rv2kuii:::iii::,:i:,, , ,,:,:i@petermu.,\n" +
                "          , :r50EZ8MBBBBGOBBBZP7::::i::,:::::,: :,:,::i;rrririiii::\n" +
                "              :jujYY7LS0ujJL7r::,::i::,::::::::::::::iirirrrrrrr:ii:\n" +
                "           ,:  :@kevensun.:,:,,,::::i:i:::::,,::::::iir;ii;7v77;ii;i,\n" +
                "           ,,,     ,,:,::::::i:iiiii:i::::,, ::::iiiir@xingjief.r;7:i,\n" +
                "        , , ,,,:,,::::::::iiiiiiiiii:,:,:::::::::iiir;ri7vL77rrirri::\n" +
                "         :,, , ::::::::i:::i:::i:i::,,,,,:,::i:i:::iir;@Secbone.ii:::");
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
