package com.hzf.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FileDownloader {

    public static void downloadFile(String fileURL) {
        try {
            // 创建URL对象
            URL url = new URL(fileURL);
            // 打开HTTP连接
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // 检查HTTP响应码
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                // 获取文件名
                if (disposition != null) {
                    // 从头部字段中提取文件名
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10, disposition.length() - 1);
                    }
                } else {
                    // 从URL中提取文件名，并进行URL解码
                    String decodedURL = URLDecoder.decode(fileURL, StandardCharsets.UTF_8.name());
                    fileName = decodedURL.substring(decodedURL.lastIndexOf("/") + 1);
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // 打开输入流
                try (InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
                     FileOutputStream fileOS = new FileOutputStream(fileName)) {
                    byte data[] = new byte[1024];
                    int byteContent;
                    // 读取数据并写入文件
                    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                        fileOS.write(data, 0, byteContent);
                    }
                } catch (IOException e) {
                    System.out.println("下载文件时出错: " + e.getMessage());
                }

                System.out.println("文件下载成功: " + fileName);
            } else {
                System.out.println("服务器返回非OK响应: " + responseCode);
            }
            httpConn.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("URL格式不正确: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("连接或读取文件时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String fileURL = "";
        downloadFile(fileURL);
    }
}


