package com.hzf.utils.Word2html;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class Word2html {

    private static final String DOC_TYPE = ".doc";
    private static final String DOCX_TYPE = ".docx";

    private static void docx2html(String fileName, String htmlPath, String htmlName) throws Exception {
        File f = new File(fileName);
        checkFile(f, DOCX_TYPE);
        //加载word文档生成 XWPFDocument对象
        InputStream in = new FileInputStream(f);
        XWPFDocument document = new XWPFDocument(in);
        //解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
        File imageFolderFile = new File(htmlPath);
        FileURIResolver fileURIResolver = new FileURIResolver(imageFolderFile);
        XHTMLOptions options = XHTMLOptions.create().URIResolver(fileURIResolver);
        FileImageExtractor fileImageExtractor = new FileImageExtractor(imageFolderFile);
        options.setExtractor(fileImageExtractor);
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);
        //将 XWPFDocument转换成XHTML
        OutputStream out = new FileOutputStream(new File(htmlPath + htmlName));
        XHTMLConverter.getInstance().convert(document, out, options);
    }

    public static void doc2html(String fileName, String htmlPath, String htmlName) throws Exception {
        File f = new File(fileName);
        checkFile(f, DOC_TYPE);
        String imagePath = htmlPath + "image\\";
        InputStream input = new FileInputStream(f);
        HWPFDocument wordDocument = new HWPFDocument(input);
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(document);
        //设置图片存放的位置
        wordToHtmlConverter.setPicturesManager((content, pictureType, suggestedName, widthInches, heightInches) -> {
            try {
                return setPic(imagePath, suggestedName, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        //解析word文档
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        File htmlFile = new File(htmlPath + htmlName);
        OutputStream outStream = new FileOutputStream(htmlFile);
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer serializer = factory.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();
    }

    private static String setPic(String imagePath, String suggestedName, byte[] content) throws Exception {
        File imgPath = new File(imagePath);
        if (!imgPath.exists())
            if (!imgPath.mkdirs())
                throw new Exception("image create err!");
        File file1 = new File(imagePath + suggestedName);
        OutputStream os = new FileOutputStream(file1);
        os.write(content);
        os.close();
        return imagePath + suggestedName;
    }

    private static void checkFile(File file, String type) throws Exception {
        if (!file.exists())
            throw new Exception("File does not Exists!");
        if (!(file.getName().endsWith(type)) && !(file.getName().endsWith(type.toUpperCase())))
            throw new Exception("File is not " + type);
    }

    public static void main(String[] args) {
        try {
            String fileName = "D:\\files\\Hadoop安装Html\\Hadoop安装.docx";
            String htmlPah = "D:\\files\\Hadoop安装Html\\";
            String htmlName = "Hadoop安装.html";
            docx2html(fileName, htmlPah, htmlName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
