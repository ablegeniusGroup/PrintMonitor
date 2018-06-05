package pos.dongwang.util;

/**
 * Created by wenjing on 2017/10/18.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


public class DateUtil {

    /** The Constant CM_LONG_DATE_FORMAT. */
    public static final String CM_LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_FORMAT = "HH:mm:ss";
    /** The Constant CM_SHORT_DATE_FORMAT. */
    public static final String CM_SHORT_DATE_FORMAT = "yyyyMMdd";
    public static final String CM_DATE_FORMAT = "yyyy-MM-dd";
    public static final String CM_DATE_FORMAT_HM = "yyyy-MM-dd HH:mm";

    /** The Constant CM_SHORT_MONTH_FORMAT. */
    public static final String CM_SHORT_MONTH_FORMAT = "yyyy-MM";
    public static final long MILLISECOND_UNIT = 1000L;
    public static final String CM_MONTH_FORMAT = "MM";

    /** The Constant CM_SHORT_YEAR_FORMAT. */
    public static final String CM_SHORT_YEAR_FORMAT = "yyyy";

    /** The Constant YEAR_MONTH. */
    public static final String YEAR_MONTH = "yyyyMM";

    /** The Constant MONTH. */
    public final static String[] MONTH = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

    /** The Constant DAY. */
    public final static String[] DAY = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    /** The date format. */
    public static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);

    public static String toDateText(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        DateFormat dateFormat = createDateFormat(pattern);
        return dateFormat.format(date);
    }

    private static DateFormat createDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.SIMPLIFIED_CHINESE);
    }
    /**
     * 取得今天的日期.
     *
     * @return the today
     */
    public static String getToday() {
        Date myDate = new Date();
        String today = DateUtil.DateToString(myDate, CM_SHORT_DATE_FORMAT);
        return today;
    }

    public static String getCurrTime() {
        Date myDate = new Date();
        String today = DateUtil.DateToString(myDate, CM_LONG_DATE_FORMAT);
        return today;
    }

    /*
    * 增加或减少指定的天數
    * */
    public static Date plusDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static String getCurrTimeYmd() {
        Date myDate = new Date();
        String today = DateUtil.DateToString(myDate, CM_DATE_FORMAT);
        return today;
    }

    public static String getCurrTimeYmdHm() {
        Date myDate = new Date();
        String today = DateUtil.DateToString(myDate, CM_DATE_FORMAT_HM);
        return today;
    }

    public static String getCurrTimeYmdHms() {
        Date myDate = new Date();
        String today = DateUtil.DateToString(myDate, CM_LONG_DATE_FORMAT);
        return today;
    }

    //判断时间date1是否在时间date2之前   时间格式 2005-4-21 16:16:34
    public static boolean isDateBefore(String date1, String date2) {
        return stringToDate(date1).before(stringToDate(date2));
    }

    public static Date stringToDate(String date) {
        Date dt = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dt = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    /**
     * 取得今天的日期.
     *
     * @return timeformat
     */
    public static long getTodayInTimeFormat() {
        Date myDate = new Date();
        long today = myDate.getTime();
        return today;
    }

    /**
     * 取得今年年份.
     *
     * @return the now year
     */
    public static String getNowYear() {
        Date myDate = new Date();
        String nowYear = DateUtil.DateToString(myDate, CM_SHORT_YEAR_FORMAT);
        return nowYear;
    }

    public static String getNowMonth() {
        Calendar c = Calendar.getInstance() ;
        c.setTime( new Date());
        int result = c.get(Calendar.MONTH) + 1;
        if(result < 10){
            return "0" + result;
        }else{
            return  "" + result;
        }

    }

    /**
     * Title : 在时间time上加上minutes分钟
     * @author : 聂水根
     * @param time

     * @return
     * @throws ParseException
     */
    public static String addSec(String time, int secs) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, secs);
        Date addDate = cal.getTime();
        return sdf.format(addDate);
    }



    /**
     * 获得当前时间.
     *
     * @return String
     */
    public static java.sql.Timestamp getNowTime() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * 取得当月的月份.
     *
     * @return the month
     */
    public static String getMonth() {
        Date myDate = new Date();
        String month = DateUtil.DateToString(myDate, CM_SHORT_MONTH_FORMAT);
        return month;
    }



    public static String secondsToText(long seconds) {
        SecondsToTextHelper secondsToTextHelper = new SecondsToTextHelper(seconds);
        return secondsToTextHelper.toText();
    }

    /**
     * 取得当月的月份.
     *
     * @return the month
     */
    public static int getCurrentMonth() {
        Date myDate = new Date();
        String month = DateUtil.DateToString(myDate, CM_MONTH_FORMAT);
        return Integer.parseInt(month);
    }

    /**
     * 取得当月的年月.
     *
     * @param ymFormat
     *            the ym format
     * @return the month
     */
    public static String getMonth(String ymFormat) {
        Date myDate = new Date();
        String month = DateUtil.DateToString(myDate, ymFormat);
        return month;
    }

    /**
     * 取得下月的月份,形式如yyyy-MM.
     *
     * @return the next month
     */
    public static String getNextMonth() {
        Date myDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, 1);

        String nextmonth = DateUtil.DateToString(cal.getTime(), CM_SHORT_MONTH_FORMAT);
        return nextmonth;
    }

    /**
     * 取得下月的月份,形式如y.
     *
     * @param ymFormat
     *            格式如:yyyyMM
     * @return the next month
     */
    public static String getNextMonth(String ymFormat) {
        Date myDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, 1);

        String nextmonth = DateUtil.DateToString(cal.getTime(), ymFormat);
        return nextmonth;
    }


    /**
     * 取得上月的月份.
     *
     * @return the up month
     */
    public static String getUpMonth() {
        Date myDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, -1);

        String nextmonth = DateUtil.DateToString(cal.getTime(), CM_SHORT_MONTH_FORMAT);
        return nextmonth;
    }

    /**
     * 取得参數指定年月的上个月.
     *
     * @param year
     *            指定年
     * @param month
     *            指定月
     * @param format
     *            指定格式 如: "yyyyMMdd"
     * @return the up month
     */
    public static String getUpMonth(String year, String month, String format) {
        Date myDate = DateUtil.getDate(year, month, "01");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, -1);

        String nextmonth = DateUtil.DateToString(cal.getTime(), format);
        return nextmonth;
    }

    /**
     * 取得参數指定年月的下个月.
     *
     * @param year
     *            指定年
     * @param month
     *            指定月
     * @param format
     *            指定格式 如: "yyyyMMdd"
     * @return the next month
     */
    public static String getNextMonth(String year, String month, String format) {
        Date myDate = DateUtil.getDate(year, month, "01");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, 1);

        String nextmonth = DateUtil.DateToString(cal.getTime(), format);
        return nextmonth;
    }

    /**
     * 取得从某一时间开始的一段年份.
     *
     * @param currdate
     *            Date
     * @param len
     *            int
     * @return List
     */
    public static List<String> getPeroidYear(Date currdate, int len) {
        List<String> lists = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        int ln = Math.abs(len);
        if (len >= 0) {
            for (int i = 0; i < len; i++) {
                cal.setTime(currdate);
                cal.add(Calendar.YEAR, i);

                String year = DateUtil.DateToString(cal.getTime(), CM_SHORT_YEAR_FORMAT);
                lists.add(year);
            }
        } else {
            for (int i = 1; i <= ln; i++) {
                cal.setTime(currdate);
                cal.add(Calendar.YEAR, -i);
                String year = DateUtil.DateToString(cal.getTime(), CM_SHORT_YEAR_FORMAT);
                lists.add(year);
            }

        }
        return lists;
    }

    /**
     * 取得明日的日期.
     *
     * @return the tomorrow
     */
    public static String getTomorrow() {
        Date myDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, 1);
        String tomorrow = DateUtil.DateToString(cal.getTime(), CM_SHORT_DATE_FORMAT);
        return tomorrow;
    }

    /**
     * 取得昨日的日期.
     *
     * @return the yesterday
     */
    public static String getYesterday() {
        Date myDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, -1);
        String yesterday = DateUtil.DateToString(cal.getTime(), CM_SHORT_DATE_FORMAT);
        return yesterday;
    }

    /**
     * 取得日期的完整打印格式.
     *
     * @param date
     *            the date
     * @return the full date string
     */
    public static String getFullDateString(String date) {
        Date myDate = DateUtil.StringToDate(date);
        return dateFormat.format(myDate);
    }

    /**
     * 日期变为字符串.
     *
     * @param date
     *            the date
     * @param iso
     *            the iso
     * @return the string
     */
    public static String DateToString(Date date, String iso) {
        SimpleDateFormat format = new SimpleDateFormat(iso);
        return format.format(date);
    }

    /**
     * 字符串变为日期.
     *
     * @param date
     *            the date
     * @return the date
     */
    public static Date StringToDate(String date) {
        Date myDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.CM_SHORT_DATE_FORMAT);
        try {
            myDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;
    }

    /**
     * 字符串变为日期.
     *
     * @param date
     *            the date
     * @param formatStr
     *            the format str
     * @return the date
     */
    public static Date StringToDate(String date, String formatStr) {
        Date myDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            myDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;
    }

    /**
     * 根据起始日期 及 间隔时间 得到结束日期.
     *
     * @param startDate
     *            起始日期
     * @param offset
     *            间隔时间
     * @return 结束日期
     */
    public static String getEndDate(String startDate, int offset) {
        Calendar cal = Calendar.getInstance();
        Date day = DateUtil.StringToDate(startDate);
        cal.setTime(day);
        cal.add(Calendar.DATE, offset);

        return DateUtil.DateToString(cal.getTime(), CM_SHORT_DATE_FORMAT);
    }

    /**
     * 根据起始日期 及 间隔时间 得到结束日期 得到的格式是yyyy-MM-dd.
     *
     * @param startDate
     *            起始日期
     * @param offset
     *            间隔时间
     * @return 结束日期
     */

    public static String getEndDateForSQLDate(String startDate, int offset) {
        Calendar cal = Calendar.getInstance();
        Date day = DateUtil.StringToDateByFormat(startDate, CM_SHORT_DATE_FORMAT);
        cal.setTime(day);
        cal.add(Calendar.DATE, offset);

        return DateUtil.DateToString(cal.getTime(), CM_SHORT_DATE_FORMAT);
    }

    /**
     * 把指定格式的字符串变为日期型.
     *
     * @param date
     *            the date
     * @param iso
     *            the iso
     * @return the date
     */
    public static Date StringToDateByFormat(String date, String iso) {
        Date myDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(iso);
        try {
            myDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;
    }

    /**
     * 取得指定年月所有星期五的日期的集合.
     *
     * @param year
     *            指定年
     * @param month
     *            指定月
     * @return list 星期五的日期的集合
     */
    public static List<String> getEndWeekDayOfMonth(String year, String month) {
        List<String> list = new ArrayList<String>();
        int days = daysInMonth(year, month);
        int weekday = 0;
        for (int i = 1; i <= days; i++) {
            weekday = getWeekOfMonth(year, month, String.valueOf(i));
            if (weekday == 5) {
                if (i < 10) {
                    list.add(year + month + "0" + String.valueOf(i));
                } else {
                    list.add(year + month + String.valueOf(i));
                }
            }
        }
        return list;
    }

    /**
     * 获得指定年月的天數.
     *
     * @param argYear
     *            the arg year
     * @param argMonth
     *            the arg month
     * @return int 天數
     */
    public static int daysInMonth(String argYear, String argMonth) {
        int year = Integer.parseInt(argYear);
        int month = Integer.parseInt(argMonth);

        GregorianCalendar c = new GregorianCalendar(year, month, 0);
        int[] daysInMonths = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        daysInMonths[1] += c.isLeapYear(c.get(GregorianCalendar.YEAR)) ? 1 : 0;
        return daysInMonths[c.get(GregorianCalendar.MONTH)];
    }

    /**
     * 得到日期中是星期几.
     *
     * @param year
     *            String
     * @param month
     *            String
     * @param day
     *            String
     * @return String
     */
    public static int getWeekOfMonth(String year, String month, String day) {
        Date myDate = getDate(year, month, day);
        int index = 0;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(myDate);
            index = cal.get(Calendar.DAY_OF_WEEK);
            if (index <= 1) {
                index = 7;
            } else {
                index = index - 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;

    }

    /**
     * 根据年月日得到日期.
     *
     * @param year
     *            String 年 YYYY
     * @param month
     *            String 月MM
     * @param day
     *            String 日dd
     * @return Date
     */
    public static Date getDate(String year, String month, String day) {
        Date result = null;
        try {
            String str = year + (month.length() == 1 ? "0" + month: month) + (day.length() == 1 ?  "0" + day: day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            result = dateFormat.parse(str);
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
        return result;
    }

    /**
     * 取得当前月的第一天.
     *
     * @return string format
     */
    public static String getFirstDayOfMonth() {
        StringBuffer buff = new StringBuffer();
        String today = DateUtil.getToday();
        buff.append(today.substring(0, 6)).append("01");
        return buff.toString();
    }

    /**
     * 取得当前月的第一天.
     *
     * @return long format
     */
    public static long getFirstDayOfMonthInTimeFormat() {
        StringBuffer buff = new StringBuffer();
        String today = DateUtil.getToday();
        buff.append(today.substring(0, 6)).append("01");
        long time = (DateUtil.StringToDateByFormat(buff.toString(), "yyyyMMdd")).getTime();

        return time;
    }

    /**
     * 取得距离当前月月底的n个月的第一天.
     *
     * @param offSet
     *            the off set
     * @return the first day of offset month
     */
    public static String getFirstDayOfOffsetMonth(int offSet) {
        StringBuffer buff = new StringBuffer();
        Date myDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, offSet);

        String nextmonth = DateUtil.DateToString(cal.getTime(), YEAR_MONTH);
        buff.append(nextmonth).append("01");

        return buff.toString();
    }

    /**
     * 把字符型时间转化成长整型时间.
     *
     * @param argStr
     *            the arg str
     * @return the long
     */
    public static long StingToLong(String argStr) {
        return (DateUtil.StringToDateByFormat(argStr, DateUtil.CM_SHORT_DATE_FORMAT)).getTime();
    }

    /**
     * 按日期格式formatStr,把字符型时间转化成长整型时间.
     *
     * @param argStr
     *            the arg str
     * @param formatStr
     *            the format str
     * @return the long
     */
    public static long StingToLong(String argStr, String formatStr) {
        return (DateUtil.StringToDateByFormat(argStr, formatStr)).getTime();
    }

    /**
     * 取得参數日期上个月的最后一天.
     *
     * @param argDate
     *            the arg date
     * @return endDateOfUpMonth 格式如"yyyyMMdd"
     */
    public static String getEndDateOfUpMonth(Date argDate) {
        StringBuffer buff = new StringBuffer();
        // java.util.Date --> String
        String date = DateUtil.DateToString(argDate, DateUtil.CM_SHORT_DATE_FORMAT);
        // 得到参數日期的上个年月
        String upMonth = DateUtil.getUpMonth(date.substring(0, 4), date.substring(4, 6), DateUtil.YEAR_MONTH);
        // 上个月的最后一天 = 上个月(yyyyMM) + 上个月总天數
        buff.append(upMonth).append(DateUtil.daysInMonth(upMonth.substring(0, 4), upMonth.substring(4)));
        date = buff.toString();
        buff = null;
        return date;
    }

    /**
     * 取得参數日期下个月的最后一天.
     *
     * @param argDate
     *            the arg date
     * @return endDateOfUpMonth 格式如"yyyyMMdd"
     */
    public static String getEndDateOfNextMonth(Date argDate) {
        StringBuffer buff = new StringBuffer();
        // java.util.Date --> String
        String date = DateUtil.DateToString(argDate, DateUtil.CM_SHORT_DATE_FORMAT);
        // 得到参數日期的上个年月
        String nextMonth = DateUtil.getNextMonth(date.substring(0, 4), date.substring(4, 6), DateUtil.YEAR_MONTH);
        // 上个月的最后一天 = 上个月(yyyyMM) + 上个月总天數
        buff.append(nextMonth).append(DateUtil.daysInMonth(nextMonth.substring(0, 4), nextMonth.substring(4)));
        date = buff.toString();
        buff = null;
        return date;
    }

    /**
     * Adds the specified (signed) amount of time to the given time field, based
     * on the calendar's rules.
     *
     * @param date
     *            the date
     * @param field
     *            the field
     * @param amount
     *            the amount
     * @return the date
     */
    public static Date add(Date date, int field, long amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, (int) amount);
        return calendar.getTime();
    }

    /**
     * Gets the first day of offset month.
     *
     * @param date
     *            the date
     * @param formatStr
     *            the format str
     * @param offSet
     *            the off set
     * @return the first day of offset month
     */
    public static String getFirstDayOfOffsetMonth(String date, String formatStr, int offSet) {
        Date myDate = DateUtil.StringToDate(date, formatStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, offSet);
        String ret = DateUtil.DateToString(cal.getTime(), DateUtil.CM_SHORT_DATE_FORMAT);
        return ret;
    }

    /**
     * Get the time several months ago.
     *
     * @param months
     * @return
     */
    public static Date getTimeMonthsAgo(int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0 - months);

        return cal.getTime();
    }


    /**
     * 得到跟myDate相隔interval个月的日期
     * @param myDate
     * @param interval
     * @return
     */
    public static Date getYear(Date myDate, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.YEAR, interval);
        return cal.getTime();
    }

    /**
     * 得到跟myDate相隔interval个月的日期
     * @param myDate
     * @param interval
     * @return
     */
    public static Date getMonth(Date myDate, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MONTH, interval);
        return cal.getTime();
    }

    /**
     * 得到跟myDate相隔interval日的日期
     * @param myDate
     * @param interval
     * @return
     */
    public static Date getDay(Date myDate, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, interval);
        return cal.getTime();
    }

    /**
     * 得到跟myDate相隔interval日的分钟
     * @param myDate
     * @param interval
     * @return
     */
    public static Date getMinute(Date myDate, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MINUTE, interval);
        return cal.getTime();
    }

    /**
     * 用多种DateFormat强转成date，转不了返回null
     * @author ZZB*/
    public static Date stringForceToDate(String date){
        Date d = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(CM_LONG_DATE_FORMAT);
            d = sdf.parse(date);
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(CM_SHORT_DATE_FORMAT);
                d = sdf.parse(date);
            } catch (ParseException e1) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(CM_DATE_FORMAT);
                    d = sdf.parse(date);
                } catch (ParseException e2) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(CM_SHORT_MONTH_FORMAT);
                        d = sdf.parse(date);
                    } catch (ParseException e3) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(CM_SHORT_YEAR_FORMAT);
                            d = sdf.parse(date);
                        } catch (ParseException e4) {
                            d = null;
                        }
                    }
                }
            }
        }
        return d;
    }

    public static String getSearchBeginTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (date != null && !"".equals(date.trim())) {
            Calendar calendar = Calendar.getInstance();
            switch (date) {
                case "7":
                    calendar.add(Calendar.DAY_OF_MONTH, -7);  //近7天内
                    break;
                case "1":
                    calendar.add(Calendar.MONTH, -1);  //近1个月内
                    break;
                case "3":
                    calendar.add(Calendar.MONTH, -3);  //近3个月内
                    break;
                case "6":
                    calendar.add(Calendar.MONTH, -6); //近6个月内
                    break;
            }
            return sdf.format(calendar.getTime());
        }
        return null;
    }


    public static int getDaysBetween(String startDate1, String endDate1) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int count = 0;

        try {
            Date startDate = df.parse(startDate1);

            Date endDate = df.parse(endDate1);
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar.setTime(startDate);
            fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
            fromCalendar.set(Calendar.MINUTE, 0);
            fromCalendar.set(Calendar.SECOND, 0);
            fromCalendar.set(Calendar.MILLISECOND, 0);

            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTime(endDate);
            toCalendar.set(Calendar.HOUR_OF_DAY, 0);
            toCalendar.set(Calendar.MINUTE, 0);
            toCalendar.set(Calendar.SECOND, 0);
            toCalendar.set(Calendar.MILLISECOND, 0);
            count= (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return count;
    }


    // 比较日期大小,第一个参數大 为true,否则为false
    public static boolean dateCompare(Date dat1, Date dat2) {
        boolean dateComPareFlag = true;
        //這裡轉換成固定格式的日期，目的是去掉時間，因為傳入的參數是Date型的數據，因此是有時間的，此處只比較日期，不比較時間
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = fmt.format(dat1);
        String date2 = fmt.format(dat2);
        try {
            dat1 = fmt.parse(date1);
            dat2 = fmt.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            dateComPareFlag = false;
        }
        if (dat1.compareTo(dat2) < 0) {
            dateComPareFlag = false; //
        }
        return dateComPareFlag;
    }


    // 比较时间大小,第一个参數大 为true,否则为false
    public static boolean timeCompare(Date dat1, Date dat2) {
        boolean dateComPareFlag = true;

        DateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        String date1 = fmt.format(dat1);
        String date2 = fmt.format(dat2);
        try {
            dat1 = fmt.parse(date1);
            dat2 = fmt.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            dateComPareFlag = false;
        }
        if (dat1.compareTo(dat2) < 0) {
            dateComPareFlag = false; //
        }
        return dateComPareFlag;
    }

    // 比较时间大小,第一个参數大 为true,否则为false
    public static boolean DatetimeCompare(Date dat1, Date dat2,String pattern) {
        boolean dateComPareFlag = true;

        DateFormat fmt = new SimpleDateFormat(pattern);
        String date1 = fmt.format(dat1);
        String date2 = fmt.format(dat2);
        try {
            dat1 = fmt.parse(date1);
            dat2 = fmt.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            dateComPareFlag = false;
        }
        if (dat1.compareTo(dat2) < 0) {
            dateComPareFlag = false; //
        }
        return dateComPareFlag;
    }




    public static String getFormatDay(Date date) {
        String patten = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(patten);
        return format.format(date);
    }

    public static String getFormatDay(Date date, String patten) {
        SimpleDateFormat format = new SimpleDateFormat(patten);
        return format.format(date);
    }


    public static String getFormatTime(Date date) {
        String patten = "HH:mm";
        SimpleDateFormat format = new SimpleDateFormat(patten);
        return format.format(date);
    }

    //通过日期得到当前星期几1-7
    public static int getWeek(Date date){
        int index = 0;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            index = cal.get(Calendar.DAY_OF_WEEK);
            if (index <= 1) {
                index = 7;
            } else {
                index = index - 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    // 01. java.util.Date --> java.time.LocalDateTime
    public static LocalDateTime UDateToLocalDateTime() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    // 03. java.util.Date --> java.time.LocalTime
    public static LocalTime UDateToLocalTime() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    public static List<String> getDaysByWeekTime(Date startDate, Date endDate, int weekTimes) {
        List<String> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        int n = 0;
        while (true) {
            String monday;
            if (weekTimes != 0) {
                cal.add(Calendar.DATE, n * 7);
            } else {
                cal.add(Calendar.DATE, n * 1);
            }
            //Monday Tuesday Wednesday Thursday Friday Saturday Sunday
            switch (weekTimes) {
                case 0:
                    break;
                case 1:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    break;
                case 2:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    break;
                case 3:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    break;
                case 4:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    break;
                case 5:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    break;
                case 6:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    break;
                case 7:
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    break;
            }
            if (cal.getTime().getTime() <= endDate.getTime()) {
                if (cal.getTime().getTime() >= startDate.getTime()) {
                    monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                    list.add(monday);
                }
            } else {
                break;
            }
            n++;
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(DateToString(new Date(), CM_LONG_DATE_FORMAT)+ "----" + DateToString(getDay(new Date(), 60), CM_LONG_DATE_FORMAT));
        List<String> list = getDaysByWeekTime(new Date(), getDay(new Date(), 60), 7);
        String str = Arrays.deepToString(list.toArray());
        System.out.println(str.substring(1, str.length() - 1).replaceAll(" ", ""));
    }

    private static class SecondsToTextHelper {

        // 365 days
        private static final long PER_YEAR_SECONDS = 31536000L;
        //30days
        private static final long PER_MONTH_SECONDS = 2592000L;

        private static final long PER_DAY_SECONDS = 86400L;

        private static final long PER_HOUR_SECONDS = 3600L;

        private static final long PER_MINUTE_SECONDS = 60L;

        private long seconds;

        public SecondsToTextHelper(long seconds) {
            this.seconds = seconds;
        }

        public String toText() {
            if (seconds < 0) {
                return "-1";
            }
            StringBuilder sb = new StringBuilder();
            toYear(sb);
            toMonth(sb);
            toDay(sb);

            toHour(sb);
            toMinute(sb);
            toSecond(sb);
            return sb.toString();
        }

        private void toSecond(StringBuilder sb) {
            final long tempSeconds = this.seconds % PER_MINUTE_SECONDS;
            sb.append(tempSeconds).append("s");
        }

        private void toMinute(StringBuilder sb) {
            if (seconds >= PER_MINUTE_SECONDS) {
                final long minuteSeconds = seconds % PER_HOUR_SECONDS;
                sb.append(minuteSeconds / PER_MINUTE_SECONDS).append("m ");
            }
        }

        private void toHour(StringBuilder sb) {
            if (seconds >= PER_HOUR_SECONDS) {
                final long hourSeconds = seconds % PER_DAY_SECONDS;
                sb.append(hourSeconds / PER_HOUR_SECONDS).append("h ");
            }
        }

        private void toDay(StringBuilder sb) {
            if (seconds >= PER_DAY_SECONDS) {
                final long daySeconds = seconds % PER_MONTH_SECONDS;
                sb.append(daySeconds / PER_DAY_SECONDS).append("d ");
            }
        }

        private void toMonth(StringBuilder sb) {
            if (seconds > PER_MONTH_SECONDS) {
                final long monthSeconds = seconds % PER_YEAR_SECONDS;
                sb.append(monthSeconds / PER_MONTH_SECONDS).append("M ");
            }
        }

        private void toYear(StringBuilder sb) {
            if (seconds >= PER_YEAR_SECONDS) {
                sb.append(seconds / PER_YEAR_SECONDS).append("y ");
            }
        }
    }
}
