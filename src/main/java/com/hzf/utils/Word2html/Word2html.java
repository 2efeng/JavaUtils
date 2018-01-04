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

    /**
     * @param fileName word文件的路径/文件名
     * @param htmlPath html路径
     * @param htmlName html文件名
     */
    public static boolean word2html(String fileName, String htmlPath, String htmlName) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) throw new Exception("File " + fileName + " does not Exists!");
        if (fileName.endsWith(".doc") || fileName.endsWith(".DOC")) {
            doc2html(file, htmlPath, htmlName);
        } else if (fileName.endsWith(".docx") || fileName.endsWith(".docx")) {
            docx2html(file, htmlPath, htmlName);
        } else {
            throw new Exception("err! the file is not word.");
        }
        return true;
    }

    /**
     * docx文件转成html
     */
    private static void docx2html(File file, String htmlPath, String htmlName) throws Exception {
        //加载word文档生成 XWPFDocument对象
        InputStream in = new FileInputStream(file);
        XWPFDocument document = new XWPFDocument(in);
        //解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
        File imageFolderFile = new File(htmlPath);
        if (!imageFolderFile.exists()) if (!imageFolderFile.mkdir())
            throw new Exception(htmlPath + "  create err!");
        FileURIResolver fileURIResolver = new FileURIResolver(imageFolderFile);
        XHTMLOptions options = XHTMLOptions.create().URIResolver(fileURIResolver);
        FileImageExtractor fileImageExtractor = new FileImageExtractor(imageFolderFile);
        options.setExtractor(fileImageExtractor);
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);
        //将 XWPFDocument转换成XHTML
        OutputStream out = new FileOutputStream(htmlPath + htmlName);
        XHTMLConverter.getInstance().convert(document, out, options);
    }

    /**
     * doc文件转成html
     */
    private static void doc2html(File file, String htmlPath, String htmlName) throws Exception {
        String imagePath = htmlPath + "image\\";
        InputStream input = new FileInputStream(file);
        HWPFDocument wordDocument = new HWPFDocument(input);
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(document);
        //设置图片存放的位置
        wordToHtmlConverter.setPicturesManager((content, pictureType, suggestedName, widthInches, heightInches) -> {
            try {
                setPic(imagePath, suggestedName, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imagePath + suggestedName;
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

    private static void setPic(String imagePath, String suggestedName, byte[] content) throws Exception {
        File imgPath = new File(imagePath);
        if (!imgPath.exists())
            if (!imgPath.mkdirs())
                throw new Exception(imagePath + "  create err!");
        File file = new File(imagePath + suggestedName);
        OutputStream os = new FileOutputStream(file);
        os.write(content);
        os.close();
    }

}
