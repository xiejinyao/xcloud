package com.xjinyao.xcloud.common.core.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.xjinyao.xcloud.common.core.exception.DateParseException;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author 谢进伟
 * @description 日期相关工具
 * @createDate 2016年12月22日 下午5:00:09
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static String ACCOUNT_PERIOD_STR = "yyyy-MM";
    public static DateTimeFormatter ACCOUNT_PERIOD_FORMATTER = DateTimeFormatter.ofPattern(ACCOUNT_PERIOD_STR);

    private static Set<DateFormatter> dateFormats = new HashSet<>();

    static {
        dateFormats.add(new DateFormatter("yyyy-MM-dd", "^\\d{4}-\\d{1,2}-\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy-MM-dd HH:mm:ss", "^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d+$"));
        dateFormats.add(new DateFormatter("yyyy-MM-dd HH:mm:ss.SSS", "^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d+.\\d+$"));

        dateFormats.add(new DateFormatter("yyyy.MM.dd", "^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy.MM.dd HH:mm", "^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy.MM.dd HH:mm:ss", "^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d+$"));
        dateFormats.add(new DateFormatter("yyyy.MM.dd HH:mm:ss.SSS", "^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d+.\\d+$"));

        dateFormats.add(new DateFormatter("yyyy/MM/dd", "^\\d{4}/\\d{1,2}/\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy/MM/dd HH:mm", "^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy/MM/dd HH:mm:ss", "^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d+$"));
        dateFormats.add(new DateFormatter("yyyy/MM/dd HH:mm:ss.SSS", "^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d+.\\d+$"));

        dateFormats.add(new DateFormatter("yyyy年MM月dd日", "^\\d{4}年\\d{1,2}月\\d{1,2}日$"));
        dateFormats.add(new DateFormatter("yyyy年MM月dd日 HH:mm", "^\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}$"));
        dateFormats.add(new DateFormatter("yyyy年MM月dd日 HH:mm:ss", "^\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d+:\\d+$"));
        dateFormats.add(new DateFormatter("yyyy年MM月dd日 HH:mm:ss.SSS", "^\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d+:\\d+.\\d+$"));

        dateFormats.add(new DateFormatter("yyyy年MM月dd日 HH时mm分", "^\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分$"));
        dateFormats.add(new DateFormatter("yyyy年MM月dd日 HH时mm分ss秒", "^\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d+秒$"));
        dateFormats.add(new DateFormatter("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒", "^\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d+秒\\d+毫秒$"));
    }

    @Data
    public static class DateFormatter {
        private String pattern;
        private String regex;

        public DateFormatter(String pattern, String regex) {
            this.pattern = pattern;
            this.regex = regex;
        }
    }

    /**
     * 将时间字符串转换成date对象
     *
     * @param dateValue 日期字符串或者时间戳字符串
     * @return
     */
    public static Date parse(String dateValue) throws DateParseException {
        if (dateValue.matches("^\\d+$")) {
            DateTime date = DateUtil.date(Long.parseLong(dateValue));
            if (date.getField(DateField.YEAR) == 1970) {
                date = DateUtil.date(Long.parseLong(dateValue) * 1000L);
            }
            return date.toJdkDate();
        }
        if (StrUtil.isNotBlank(dateValue)) {
            dateValue = dateValue.replaceAll("[\\s+T]", " ").replaceAll("Z", "");
            if (dateValue.matches("^\\d{4}(-|/|年|\\.)\\d{1,2}月?$")) {
                if (dateValue.contains("-")) {
                    dateValue = dateValue + "-01";
                } else if (dateValue.contains("/")) {
                    dateValue = dateValue + "/01";
                } else if (dateValue.contains(".")) {
                    dateValue = dateValue + ".01";
                } else if (dateValue.contains("年")) {
                    if (dateValue.contains("月")) {
                        dateValue = dateValue + "01日";
                    } else {
                        dateValue = dateValue + "月01日";
                    }
                }
            }
        }
        return parse(dateValue, Collections.emptySet());
    }

    /**
     * 日期字符串转日期
     *
     * @param date    日期字符串
     * @param pattern 格式化
     */
    public static LocalDate parseLocalDate(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 时间字符串转时间
     *
     * @param time    时间字符串
     * @param pattern 格式化
     */
    public static LocalTime parseLocalTime(String time, String pattern) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期时间字符串转日期时间
     *
     * @param dateTime 日期时间字符串
     * @param pattern  格式化
     */
    public static LocalDateTime parseLocalDateTime(String dateTime, String pattern) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime 格式化
     * 比如：pattern为"yyyy-MM-dd HH:mm:ss"，就是格式化去除毫纳秒数
     *
     * @param dateTime 日期时间
     * @param pattern 格式化
     * @return 格式化过后的日期时间
     */
    public static LocalDateTime localDateTimeFormat(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        String localDateTimeFormat = df.format(dateTime);
        return LocalDateTime.parse(localDateTimeFormat, df);
    }

    /**
     * 将一个Date类型转换成LocalDateTime类型（采用24小时制）
     *
     * @param date 需要转换的Date对象
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        String formatStr = format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String[] sp = formatStr.split(" ");
        LocalDate localDate = LocalDate.parse(sp[0]);
        LocalTime localTime = LocalTime.parse(sp[1]);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 将一个Date类型转换成LocalDate类型
     *
     * @param date 需要转换的Date对象
     * @return
     */
    public static LocalDate toLocalDate(Date date) {
        DateTime dateTime = DateTime.of(date);
        return LocalDate.of(dateTime.year(), dateTime.month() + 1, dateTime.dayOfMonth());
    }

    /**
     * 将一个LocalDate类型转换成String类型
     *
     * @param date 需要转换的Date对象
     * @return
     */
    public static String toStringLocalDate(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String format = dateTimeFormatter.format(date);
        return format;
    }

    /**
     * 将一个LocalDateTime类型转换成String类型
     *
     * @param date 需要转换的String对象
     * @return
     */
    public static String toStringByLocalDateTime(LocalDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String format = dateTimeFormatter.format(date);
        return format;
    }
    /**
     * 将一个LocalDateTime类型转换成String类型
     *
     * @param date 需要转换的String对象
     * @return
     */
    public static String toStringHmsByLocalDateTime(LocalDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = dateTimeFormatter.format(date);
        return format;
    }

    /**
     * 将时间字符串 转localDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String date) {
        try {
            return toLocalDateTime(parse(date));
        } catch (DateParseException e) {
            System.out.println(date);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将时间字符串转换成date对象
     *
     * @param dateValue   日期字符串
     * @param dateFormats dateValue 可能的格式
     * @return
     * @throws DateParseException
     */
    public static Date parse(String dateValue, Collection<DateFormatter> dateFormats) throws DateParseException {
        if (dateFormats == null || dateFormats.isEmpty()) {
            dateFormats = DateUtil.dateFormats;
        }
        for (DateFormatter dateFormat : dateFormats) {
            if (dateValue.matches(dateFormat.getRegex())) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat.getPattern());
                    return simpleDateFormat.parse(dateValue);
                } catch (ParseException e) {
                }
            }
        }
        throw new DateParseException("Unable to parse the date " + dateValue);
    }

    /**
     * 数据曲线查询获取时间段
     *
     * @param start
     * @param end
     * @param interval
     * @return
     */
    public static List<String> getIntervalTimeList(String start, String end, int interval) {
        Date startDate = convertString2Date("yyyy-MM-dd HH:mm:ss", start);
        Date endDate = convertString2Date("yyyy-MM-dd HH:mm:ss", end);
        List<String> list = new ArrayList<>();
        while (startDate.getTime() <= endDate.getTime()) {
            list.add(convertDate2String("yyyy-MM-dd HH:mm:ss", startDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR, interval);
            if (calendar.getTime().getTime() > endDate.getTime()) {
                if (!startDate.equals(endDate)) {
                    list.add(convertDate2String("yyyy-MM-dd HH:mm:ss", endDate));
                }
                startDate = calendar.getTime();
            } else {
                startDate = calendar.getTime();
            }

        }
        return list;
    }

    private static Date convertString2Date(String format, String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertDate2String(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 根据时间格式获取当前时间
     *
     * @param format 时间格式
     * @return 当前时间字符串
     */
    public static String getCurrentDateStr(String format) {
        return convertDate2String(format, new Date());
    }

    /**
     * 得到N天前的日期
     *
     * @param date
     * @param nDayNum
     * @return
     */
    public static Date beforeNDaysDate(Date date, Integer nDayNum) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = formatter.parse(dateToString(date));
            long t1 = d.getTime();
            long t3 = nDayNum - 1;
            long t2 = t3 * 24 * 60 * 60 * 1000;
            d.setTime(t1 - t2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringToDate(formatter.format(d));
    }

    /**
     * 将时间字符串转化为  yyyy-MM-dd HH:mm:ss 时间
     *
     * @param pstrString
     * @return
     */
    public static Date stringToDate(String pstrString) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date toDate = null;
        try {
            toDate = sdf.parse(pstrString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toDate;
    }

    /**
     * 将时间转化为   yyyy-MM-dd HH:mm:ss 字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (sdf.format(date));
    }

    /**
     * 获取时间查询菜单
     *
     * @return
     */
    public static List<String> getDateMenus(String startTime, String endTime, Integer num) {
        //定义一个List<Date>集合，存储所有时间段
        List<String> dates = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //解析前台传回的时间
        if (startTime.equals(endTime)) {
            try {
                Date parse = format.parse(startTime);
                Date date = toDayStartHour(parse);
                if (num != 24) {
                    for (int i = 0; i < 24; i += num) {
                        dates.add(date2Str(addDateHour(date, i)));
                    }
                } else {
                    dates.add(date2Str(addDateHour(date, 24)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Date parse = format.parse(startTime);
                Date parse1 = format.parse(endTime);
                Date start = toDayStartHour(parse);
                Date end1 = toDayStartHour(parse1);
                Date end = addDateHour(end1, 24);
//                根据传回时间计算时间段
                long l = (end.getTime() - start.getTime()) / (1000 * 60 * 60);
                for (long i = 0; i < l; i += num) {
                    dates.add(date2Str(addDateHour(start, (int) i)));
                }
                Iterator<String> it = dates.iterator();
                while (it.hasNext()) {
                    Date time = format.parse(it.next());
                    if (time.getTime() > parse1.getTime()) {
                        it.remove();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    /**
     * 获取指定日期的凌晨
     *
     * @return
     */
    public static Date toDayStartHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();
        return start;
    }

    /**
     * 时间转成yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String date2Str(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 时间递增N小时
     *
     * @param hour
     * @return
     */
    public static Date addDateHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hour);// 24小时制
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取时间字符串当天凌晨的开始时间字符串
     *
     * @param date
     * @return
     */
    public static String toDayStartHour(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = null;
        try {
            Date parse = dateFormat.parse(date);
            Date date2 = toDayStartHour(parse);
            date1 = dateFormat.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    /**
     * 获取时间字符串隔天凌晨的开始时间字符串
     *
     * @param date
     * @return
     */
    public static String toNextDayStartHour(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = null;
        try {
            Date parse = dateFormat.parse(date);
            Date date2 = toDayStartHour(parse);
            Date date3 = addDateHour(date2, 24);
            date1 = dateFormat.format(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    /**
     * LocalDateTime 转 Date 获取第二天凌晨的时间
     *
     * @param localdatetime
     * @return
     */
    public static Date LocalDateTime2NextDayDate(LocalDateTime localdatetime) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = localdatetime;
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date from = Date.from(zdt.toInstant());
        Date date = addDateHour(from, 24);
        Date date1 = toDayStartHour(date);
        return date1;
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转  LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 获取date之前几小时的时间
     *
     * @param hours
     * @param date
     * @return
     */
    public static String getBeforeHoursTime(double hours, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date toDate = null;
        try {
            toDate = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double v = toDate.getTime() - hours * 60 * 60 * 1000;
        return sdf.format(new Date((long) v));
    }

    /**
     * 求两个时间戳的差，并组装成一个对象
     *
     * @param startMillis 开始时间戳
     * @param endMillis   结束时间戳
     * @return
     */
    public static DiffFormatDuring diffFormatDuring(long startMillis, long endMillis) {
        long mss = endMillis - startMillis;
        return diffFormatDuring(mss);
    }

    /**
     * 求两个时间戳的差，并组装成一个对象
     *
     * @param mss 两个事件的差值
     * @return
     */
    public static DiffFormatDuring diffFormatDuring(long mss) {
        int onDayMillisecond = 1000 * 60 * 60 * 24;
        int days = (int) (mss / onDayMillisecond);
        int hours = (int) ((mss % onDayMillisecond) / (1000 * 60 * 60));
        int minutes = (int) ((mss % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (mss % (1000 * 60)) / 1000;
        return new DiffFormatDuring(days, hours, minutes, seconds);
    }

    /**
     * 根据当前时间获取指定时间段
     *
     * @param hours 时间间隔，需要大于当前时间填正数，小于填负数
     * @return LocalDateTime[1] 为当前时间取整点
     */
    public static LocalDateTime[] getTimeSlot(LocalDateTime now, long hours) {
        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        LocalDateTime startTime = endTime.plusHours(hours);
        LocalDateTime[] localDateTimes = {startTime, endTime};
        return localDateTimes;
    }

    public static class DiffFormatDuring {
        public int days;
        public int hours;
        public int minutes;
        public int seconds;

        public DiffFormatDuring(int days, int hours, int minutes, int seconds) {
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        @Override
        public String toString() {
            return days + " 天 " + hours + " 小时 " + minutes + " 分钟 " + seconds + " 秒 ";
        }
    }

    /**
     * 判断当前时间是否在指定时间范围
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 结果
     */
    public static boolean nowIsInBetween(LocalTime from, LocalTime to) {
        LocalTime now = LocalTime.now();
        return now.isAfter(from) && now.isBefore(to);
    }
}
