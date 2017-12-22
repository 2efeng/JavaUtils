package com.hzf.utils.HttpClientUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private static final String UTF_8 = "UTF-8";

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(3000)
            .build();

    public static String doGet(String url)
            throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        HttpResponse response = httpClient.execute(httpGet);
        return getResult(response);
    }

    public static String doPost(String url, String json)
            throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        StringEntity entity = new StringEntity(json, UTF_8);
        entity.setContentType("application/json");
        entity.setContentEncoding("gzip");
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        return getResult(response);
    }

    private static String getResult(HttpResponse response)
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
}
