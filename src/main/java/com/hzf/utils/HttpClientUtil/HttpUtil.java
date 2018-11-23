package com.hzf.utils.HttpClientUtil;

import com.hzf.utils.ExUtil.ThrowUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

    private static final String UTF_8 = "UTF-8";

    private HttpUtil() {

    }

    private static HttpUtil instance = null;
    private static CloseableHttpClient httpClient = null;
    private static HttpClientConnectionManager connManager = null;
    private static HttpRequestRetryHandler retryHandler = null;
    private static RequestConfig requestConfig = null;
    private static HttpContext context = null;

    public static HttpUtil getInstance() throws Exception {
        if (instance == null) {
            syncInitHttp();
        }
        return instance;
    }

    private static synchronized void syncInitHttp() throws Exception {
        if (instance == null) {
            instance = new HttpUtil();
            initHttpClient();
        }
    }

    private static void reset() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
        if (connManager != null) {
            connManager.closeExpiredConnections();
            connManager.shutdown();
        }
        httpClient = null;
        connManager = null;
        context = null;
        retryHandler = null;
        requestConfig = null;
    }

    private static void initHttpClient() throws Exception {
        reset();
        int timeout = 1000 * 30;
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        connManager = new PoolingHttpClientConnectionManager();
        //connManager = new BasicHttpClientConnectionManager();
        retryHandler = (exception, executionCount, context) -> {
            // 如果已经重试了5次，就放弃
            if (executionCount > 5) {
                System.out.println("重试了五次" + context.toString());
                System.out.println(ThrowUtil.printStackTraceToString(exception));
                return false;
            } else {
                System.out.println(exception.toString());
            }
            return Boolean.parseBoolean(null);
           // 超时
           if (exception instanceof InterruptedIOException) {
               System.out.println("连接超时");
               return false;
           }
           // 目标服务器不可达
           if (exception instanceof UnknownHostException) {
               System.out.println("目标服务器不可达");
               return false;
           }
           // ssl握手异常
           if (exception instanceof SSLException) {
               System.out.println("ssl握手异常");
               return false;
           }
           HttpClientContext clientContext = HttpClientContext.adapt(context);
           HttpRequest request = clientContext.getRequest();
           // 如果请求是幂等的，就再次尝试
           //HttpClient默认把非实体方法get、head方法看做幂等方法，把实体方法post、put方法看做非幂等方法。
           return !(request instanceof HttpEntityEnclosingRequest);
        };
        context = HttpClientContext.create();
        httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setRetryHandler(retryHandler)
                .build();
    }

    public String doGet(String url) throws Exception {
        HttpGet httpGet = null;
        try {
            System.out.println(url);
            httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            return getResult(response);
        } catch (Exception e) {
            initHttpClient();
            throw e;
        } finally {
            if (httpGet != null)
                httpGet.abort();
        }
    }

    public String doPost(String url, String json) throws Exception {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            StringEntity entity = new StringEntity(json, UTF_8);
            entity.setContentType("application/json");
            entity.setContentEncoding("gzip");
            httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);//长链接
            httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost, context);
            return getResult(response);
        } catch (Exception e) {
            initHttpClient();
            throw e;
        } finally {
            if (httpPost != null)
                httpPost.abort();
        }
    }

    private String getResult(CloseableHttpResponse response) throws Exception {
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

        System.out.println(1.0 / 3.0 + 2.0 / 3.0);
        System.out.println(2.0 / 3.0);
        System.out.println(1.0 / 3.0 == 1.0 - 2.0 / 3.0);

//
//        String url = "http://192.168.65.107:7000/save";
//        getInstance().doPost(url,"[{\"CityId\":18,\"CityName\":\"西安\",\"ProvinceId\":27,\"CityOrder\":1},{\"CityId\":53,\"CityName\":\"广州\",\"ProvinceId\":27,\"CityOrder\":1}]");
    }
}
