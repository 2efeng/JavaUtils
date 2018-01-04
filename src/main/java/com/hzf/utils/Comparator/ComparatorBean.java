package com.hzf.utils.Comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ComparatorBean implements Comparator<Bean> {

    private boolean asc;

    private ComparatorBean(boolean asc) {
        this.asc = asc;
    }

    @Override
    public int compare(Bean bean1, Bean bean2) {
        int bean1Num = Integer.valueOf(bean1.getNumStr());
        int bean2Num = Integer.valueOf(bean2.getNumStr());
        if (asc) return asc(bean1Num, bean2Num);
        else return desc(bean1Num, bean2Num);
    }

    private int asc(int bean1Num, int bean2Num) {
        if (bean1Num < bean2Num) return -1;
        else return 0;
    }

    private int desc(int bean1Num, int bean2Num) {
        if (bean1Num < bean2Num) return 1;
        else return 0;
    }

    public static List<Bean> sort(List<Bean> list) {
        int len = list.size();
        if (len == 0) return list;
        Bean[] arr = new Bean[list.size()];
        for (int i = 0; i < len; i++)
            arr[i] = list.get(i);
        ComparatorBean mComparator = new ComparatorBean(true);
        Arrays.sort(arr, mComparator);
        return Arrays.asList(arr);
    }


}
