package com.hzf.utils;

import com.hzf.utils.FTPUtil.FTPUtil;

public class Main {

    public static void main(String[] args) throws Exception {


        boolean result = FTPUtil.getInstance().removeDirectory("/HBaseClient");

        System.out.println(result);

    }
}
