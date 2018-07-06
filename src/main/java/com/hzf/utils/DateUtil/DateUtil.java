package com.hzf.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    //region 时间格式
    /*
        Z yyyy-MM-dd HH:MM:ss

        世界时(UT 或 UTC)
        格林威治时间 (GMT)
        GMT和UT是相等的
        UTC 和 UT 的区别是：UTC 是基于原子时钟的，UT 是基于天体观察的

        dow 是一周中的某一天 (Sun, Mon, Tue, Wed, Thu, Fri, Sat)
        mon 是月份 (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
        dd 是一月中的某一天（01 至 31），显示为两位十进制数。
        hh 是一天中的小时（00 至 23），显示为两位十进制数。
        mm 是小时中的分钟（00 至 59），显示为两位十进制数。
        ss 是分钟中的秒数（00 至 61），显示为两位十进制数。 60,61 是因为有"润秒"
        zzz 是时区（并可以反映夏令时）。标准时区缩写包括方法 parse 识别的时区缩写。 如果不提供时区信息，则 zzz 为空，即根本不包括任何字符。
        yyyy 是年份，显示为 4 位十进制数。

        G Era 标志符 Text AD
        y 年 Year 1996; 96
        M 年中的月份 Month July; Jul; 07
        w 年中的周数 Number 27
        W 月份中的周数 Number 2
        D 年中的天数 Number 189
        d 月份中的天数 Number 10
        F 月份中的星期 Number 2
        E 星期中的天数 Text Tuesday; Tue
        a Am/pm 标记 Text PM
        H 一天中的小时数（0-23） Number 0
        k 一天中的小时数（1-24） Number 24
        K am/pm 中的小时数（0-11） Number 0
        h am/pm 中的小时数（1-12） Number 12
        m 小时中的分钟数 Number 30
        s 分钟中的秒数 Number 55
        S 毫秒数 Number 978
        z 时区 General time zone Pacific Standard Time; PST; GMT-08:00
        Z 时区 RFC 822 time zone -0800

        日期和时间模式 结果
        "yyyy.MM.dd G 'at' HH:mm:ss z" 2001.07.04 AD at 12:08:56 PDT
        "EEE, MMM d, ''yy" Wed, Jul 4, '01
        "h:mm a" 12:08 PM
        "hh 'o''clock' a, zzzz" 12 o'clock PM, Pacific Daylight Time
        "K:mm a, z" 0:08 PM, PDT
        "yyyyy.MMMMM.dd GGG hh:mm aaa" 02001.July.04 AD 12:08 PM
        "EEE, d MMM yyyy HH:mm:ss Z" Wed, 4 Jul 2001 12:08:56 -0700
        "yyMMddHHmmssZ" 010704120856-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ" 2001-07-04T12:08:56.235-0700

     */
    //endregion

    /**
     * Date类型转为指定格式的String类型
     */
    public static String date2str(Date source, String pattern) {
        if (simpleDateFormat == null) simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(source);
    }

    /**
     * String转为Date
     */
    public static Date str2date(String date, String pattern) throws Exception {
        if (simpleDateFormat == null) simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.parse(date);
    }

    /**
     * @param y 年
     * @param m 月
     * @param d 日
     */
    public static Date getDate(int y, int m, int d) throws Exception {
        return getDate(y, m, d, 0, 0, 0, 0);
    }

    /**
     * @param y  年
     * @param m  月
     * @param d  日
     * @param h  时
     * @param mi 分
     */
    public static Date getDate(int y, int m, int d, int h, int mi) throws Exception {
        return getDate(y, m, d, h, mi, 0, 0);
    }

    /**
     * @param y  年
     * @param m  月
     * @param d  日
     * @param h  时
     * @param mi 分
     * @param s  秒
     */
    public static Date getDate(int y, int m, int d, int h, int mi, int s) throws Exception {
        return getDate(y, m, d, h, mi, s, 0);
    }

    /**
     * @param y  年
     * @param m  月
     * @param d  日
     * @param h  时
     * @param mi 分
     * @param s  秒
     * @param ms 毫秒
     */
    public static Date getDate(int y, int m, int d, int h, int mi, int s, int ms) throws Exception {
        checkDate(y, m, d, h, mi, s, ms);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m - 1);
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, mi);
        cal.set(Calendar.SECOND, s);
        cal.set(Calendar.MILLISECOND, ms);
        return cal.getTime();
    }

    /**
     * 判断是不是闰年
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }

    /**
     * 获取当前时间字符串
     */
    public static String getCurrentDate(String pattern) {
        if (simpleDateFormat == null) simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获得当前unix时间戳(毫秒)
     * 可以算出时间点
     *
     * @return 当前unix时间戳
     */
    public static long currentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 返回精确的计时，系统计时器的当前值，以毫微秒为单位
     * 可以用来计算时间差
     * 不同jvm的初始时间可能不一样？？？
     * 好像随机选择一个开始时间点， 因此无法根据其他地方的的值算出时间点
     */
    public static long getSysNanoTime() {
        return System.nanoTime();
    }

    private static void checkDate(int y, int m, int d, int h, int mi, int s, int ms) throws Exception {
        switch (m) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (d < 1 || d > 30) throw new Exception("day error!");
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (d < 1 || d > 31) throw new Exception("day error!");
                break;
            case 2:
                if (isLeapYear(y)) {
                    if (d < 1 || d > 29) throw new Exception("day error!");
                } else {
                    if (d < 1 || d > 28) throw new Exception("day error!");
                }
                break;
            default:
                throw new Exception("month error!");
        }
        if (h < 0 || h > 23) throw new Exception("hour error!");
        if (mi < 0 || mi > 59) throw new Exception("minute error!");
        if (s < 0 || s > 59) throw new Exception("second error!");
        if (ms < 0 || ms > 999) throw new Exception("millisecond error!");
    }

    public static void main(String[] args) throws InterruptedException {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2017, Calendar.MARCH, 2);
//        for (int i = 0; i < 10; i++) {
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            System.out.println(simpleDateFormat.format(calendar.getTime()));
//        }

        Calendar start = Calendar.getInstance();
        start.set(2017, Calendar.FEBRUARY, 2);
        Calendar end = Calendar.getInstance();
        end.set(2017, Calendar.DECEMBER, 3);
        while (start.getTime().getTime() <= end.getTime().getTime()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String Date = simpleDateFormat.format(start.getTime());
            for (int h = 8; h < 12; h++) {
                System.out.println(Date);
            }
            start.add(Calendar.DAY_OF_MONTH, 1);
        }

    }


}
