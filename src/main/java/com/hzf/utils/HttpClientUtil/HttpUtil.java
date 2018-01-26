package com.hzf.utils.HttpClientUtil;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;

public class HttpUtil {

    //TODO LOG

    private static final String UTF_8 = "UTF-8";

    private static int timeout = 1000 * 30;
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(timeout)
            .setConnectTimeout(timeout)
            .setSocketTimeout(timeout)
            .build();

    private HttpUtil() {
    }

    private static HttpUtil instance = null;
    private static CloseableHttpClient httpClient = null;
    private static HttpClientConnectionManager connManager = null;
    private static HttpRequestRetryHandler myRetryHandler = null;

    public static HttpUtil getInstance() {
        if (instance == null) {
            httpClient = null;
            connManager = null;
            myRetryHandler = null;
            syncInitHttp();
        }
        return instance;
    }

    private static synchronized void syncInitHttp() {
        if (instance == null) {
            instance = new HttpUtil();
            connManager = new PoolingHttpClientConnectionManager();
            myRetryHandler = (exception, executionCount, context) -> {
                // 如果已经重试了5次，就放弃
                if (executionCount > 5) return false;
                // 超时
                if (exception instanceof InterruptedIOException) return false;
                // 目标服务器不可达
                if (exception instanceof UnknownHostException) return false;
                // ssl握手异常
                if (exception instanceof SSLException) return false;
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                return !(request instanceof HttpEntityEnclosingRequest);
            };
            httpClient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setRetryHandler(myRetryHandler)
                    .build();

        }
    }

    public String doGet(String url)
            throws Exception {
        //TODO url to log.log
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        HttpResponse response = httpClient.execute(httpGet);
        return getResult(response);
    }

    public String doPost(String url, String json)
            throws Exception {
        //TODO url json to log.log
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        StringEntity entity = new StringEntity(json, UTF_8);
        entity.setContentType("application/json");
        entity.setContentEncoding("gzip");
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);//长链接
        httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        return getResult(response);
    }

    private String getResult(HttpResponse response)
            throws Exception {
        String result = null;
        if (response != null) {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, UTF_8);
                EntityUtils.consume(resEntity);
            }
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("www.baidu2.com")
                .build();

        System.out.println(uri.toString());
        getInstance().doGet(uri.toString());

    }
}
