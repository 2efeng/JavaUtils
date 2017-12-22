package com.hzf.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 时区 年-月-日 时:分:秒 显示格式
     */
    public static String DATE_TO_STRING_DETAILS_PATTERN = "Z yyyy-MM-dd HH:MM:ss";


    /**
     * Date类型转为指定格式的String类型
     *
     * @param source DATE
     */
    public static String dateToString_details(Date source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TO_STRING_DETAILS_PATTERN);
        return simpleDateFormat.format(source);
    }

    /**
     * @param y  年
     * @param m  月
     * @param d  日
     * @param h  时
     * @param mi 分
     * @param s  秒
     * @return 日期
     */
    static Date getDate(int y, int m, int d, int h, int mi, int s) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, mi - 1);
        cal.set(Calendar.DATE, d);
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        cal.set(Calendar.SECOND, s);
        return cal.getTime();
    }


    /**
     * 获得当前unix时间戳(毫秒)
     *
     * @return 当前unix时间戳
     */
    public static long currentTimeStamp() {
        return System.currentTimeMillis();
    }


}
