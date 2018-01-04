package com.hzf.utils.Comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Bean bean1 = new Bean(549, "549");
        Bean bean2 = new Bean(123, "123");
        Bean bean3 = new Bean(659, "659");
        Bean bean4 = new Bean(99, "99");
        List<Bean> list = new ArrayList<>();
        list.add(bean1);
        list.add(bean2);
        list.add(bean3);
        list.add(bean4);
        for (Bean bean : list)
            System.out.println(bean.getNumStr());

        System.out.println("~~~~~~~~~");

        for (Bean bean : ComparatorBean.sort(list))
            System.out.println(bean.getNumStr());


    }
}
