package com.hzf.utils.FileUtil;

import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.*;

public class FileUtil {

    public static final int FILE_TYPE_UNDEFINED = 0;

    public static final int FILE_TYPE_TXT = 1;

    public static final int FILE_TYPE_DOC = 2;

    public static final int FILE_TYPE_DOCX = 3;

    public static final int FILE_TYPE_PDF = 4;

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
     * 读本地文件,获取文件流
     */
    public static FileInputStream readFile(String fileName) throws Exception {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            return new FileInputStream(file);
        } else {
            throw new Exception("not found file!");
        }
    }


    public static String printFileContent(String fileName) throws Exception {
        int fileType = FILE_TYPE_UNDEFINED;
        if (fileName.endsWith(".txt") || fileName.endsWith(".TXT")) fileType = FILE_TYPE_TXT;
        else if (fileName.endsWith(".doc") || fileName.endsWith(".DOC")) fileType = FILE_TYPE_DOC;
        else if (fileName.endsWith(".docx") || fileName.endsWith(".DOCX")) fileType = FILE_TYPE_DOCX;
        else if (fileName.endsWith(".pdf") || fileName.endsWith(".PDF")) fileType = FILE_TYPE_PDF;
        return printFileContent(fileName, fileType);
    }

    public static String printFileContent(String fileName, int fileType) throws Exception {
        FileInputStream in = readFile(fileName);
        switch (fileType) {
            case FILE_TYPE_DOC:
                return getDocContent(in);
            case FILE_TYPE_DOCX:
                return getDocxContent(in);
            case FILE_TYPE_PDF:
                return getPdfContent(in);
            case FILE_TYPE_UNDEFINED:
            case FILE_TYPE_TXT:
            default:
                return getTxtContent(in);
        }
    }

    private static String getTxtContent(InputStream in) throws Exception {
        StringBuilder s = new StringBuilder();
        InputStreamReader read = null;
        BufferedReader reader = null;
        String line;
        try {
            read = new InputStreamReader(in, "GBK");
            reader = new BufferedReader(read);
            while ((line = reader.readLine()) != null) s.append(line).append("\n");
        } finally {
            if (reader != null) reader.close();
            if (read != null) read.close();
            if (in != null) in.close();
        }
        return s.toString();
    }

    private static String getDocContent(InputStream in) throws Exception {
        StringBuilder s = new StringBuilder();
        WordExtractor ex = null;
        try {
            ex = new WordExtractor(in);
            s.append(ex.getText());
        } finally {
            if (ex != null) ex.close();
            if (in != null) in.close();
        }
        return s.toString();
    }

    private static String getDocxContent(InputStream in) throws Exception {
        StringBuilder s = new StringBuilder();
        OPCPackage opcPackage = null;
        POIXMLTextExtractor extractor = null;
        try {
            opcPackage = OPCPackage.open(in);
            extractor = new XWPFWordExtractor(opcPackage);
            s.append(extractor.getText());
        } finally {
            if (extractor != null) extractor.close();
            if (opcPackage != null) opcPackage.close();
            if (in != null) in.close();
        }
        return s.toString();
    }

    private static String getPdfContent(FileInputStream in) throws Exception {
        PDDocument document = null;
        try {
            PDFParser parser = new PDFParser((RandomAccessRead) in);
            parser.parse();
            document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } finally {
            if (document != null) document.close();
            if (in != null) in.close();
        }
    }

}
