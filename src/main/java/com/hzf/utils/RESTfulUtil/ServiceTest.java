package com.hzf.utils.RESTfulUtil;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceTest implements Runnable {

    private boolean runFlag = true;

    /**
     * 设定服务线程运行标志值
     */
    public synchronized void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    /**
     * 取得服务线程运行标志值
     */
    private synchronized boolean getRunFlag() {
        return runFlag;
    }

    @Override
    public void run() {
        System.out.println("~~~~~~~~~0 Run Thread~~~~~~~~~");
        // 添加 ResourceClass
        List<Class<?>> resourceClassList = new ArrayList<Class<?>>();
        resourceClassList.add(ProductServiceImpl.class);

        // 添加 ResourceProvider
        List<ResourceProvider> resourceProviderList = new ArrayList<ResourceProvider>();
        resourceProviderList.add(new SingletonResourceProvider(new ProductServiceImpl()));

        // 添加 Provider
        List<Object> providerList = new ArrayList<Object>();
        providerList.add(new JacksonJsonProvider());

        System.out.println("~~~~~~~~~1 Run Thread~~~~~~~~~");
        // 发布 REST 服务
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setAddress("http://192.168.65.107:8080");
        factory.setResourceClasses(resourceClassList);
        factory.setResourceProviders(resourceProviderList);
        factory.setProviders(providerList);
        System.out.println("~~~~~~~~~2 Run Thread~~~~~~~~~");
        factory.create();
        System.out.println("rest ws is published");
    }

    /**
     * 获取当前时间
     */
    public String getLocalTime() {
        String time = "[";
        SimpleDateFormat df = new SimpleDateFormat("Z yyyy-MM-dd HH:mm:ss");
        time += df.format(new Date());
        time += "]";
        return time;
    }
}
