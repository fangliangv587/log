package com.cenco.log;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();

	public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_YMDHMS1 = "yyyyMMddHHmmss";
	public static final String FORMAT_YMD = "yyyy-MM-dd";
    public static final String FORMAT_YMD2 = "yyyyMMdd";
    public static final String FORMAT_YMD1 = "yyyy年MM月dd日";
    public static final String FORMAT_MD1 = "MM-dd";
	public static final String FORMAT_MD2 = "MM/dd";
	public static final String FORMAT_HMS = "HH:mm:ss";
	public static final String FORMAT_HM = "HH:mm";
	public static final String FORMAT_M = "mm";
	public static final String FORMAT_S = "ss";

	public static final  String[] WEEKDAYS1 = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	public static final  String[] WEEKDAYS2 = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static final int MINUTE = 60 * 1000;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;


	/**
	 * 判断给定年份是不是闰年
	 *
	 * @return true 是闰年，false不是
	 */
	public static boolean isLeapYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return isLeapYear(year);
    }

	/**
	 * 判断是否是闰年
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0);
	}


	public static Date getFormatDate(Date date, String format){
        String dateString = getDateString(date, format);
        Date date1 = getDate(dateString, format);
        return date1;
    }

	/**
	 * date 转 string
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateString(Date date, String format) {
	    if (date == null || format == null){
	        return null;
        }
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}

	public static String getDateString(String format) {
		return getDateString(new Date(), format);
	}

	public static String getDateString() {
		return getDateString(FORMAT_YMDHMS);
	}

	public static String getDateString(long time, String format){
        Date date = new Date();
        date.setTime(time);
        return getDateString(date,format);
    }

    public static String getDateString(long time){
        Date date = new Date();
        date.setTime(time);
        return getDateString(date,FORMAT_YMDHMS);
    }

	public static Date getDate(String str, String format){
	    if (str == null || format == null){
	        return null;
        }
		try {
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			Date date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e("util",e.getMessage());
		}
		return null;
	}



	public static int getDateColumeValue(Date date, int colume){
		if (date==null){
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
        int value = cal.get(colume);
        if (colume == Calendar.MONTH){
            value += 1;
        }
        return value;
	}

    /**
     * 请确认年月日有效
     * @param year  0-10000
     * @param month 1-12
     * @param day 不固定，<=31
     * @param hour
     * @param minute
     * @param second
     * @return
     */
	public static Date createDate(int year, int month, int day, int hour, int minute, int second){
	    String err = "请确认年月日有效";
	    if (year>10000 || year < 0){
            LogUtils.w("util",err);
	        return null;
        }
        if (month<=0 || month>12){
            LogUtils.w("util",err);
	        return null;
        }
        //未完善
        if (day>31 || day<= 0){
            LogUtils.w("util",err);
            return null;
        }

        if (hour<0 || hour>23){
            LogUtils.w("util",err);
            return null;
        }

        if (minute<0 || minute>59){
            LogUtils.w("util",err);
            return null;
        }
        if (second<0 || second>59){
            LogUtils.w("util",err);
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day,hour,minute,second);
        return calendar.getTime();
    }

    public static Date createYMDDate(int year, int month, int day){
	    return createDate(year, month, day,0,0,0);
    }
    public static Date createHMSDate(int hour, int minute, int second){
	    return createDate(1, 1, 1,hour,minute,second);
    }

	public static int getYear(Date date){
		return getDateColumeValue(date,Calendar.YEAR);
	}
	public static int getMonth(Date date){
        return getDateColumeValue(date,Calendar.MONTH);
	}
	public static int getDay(Date date){
        return getDateColumeValue(date,Calendar.DATE);
    }

	public static int getHour(Date date){
        return getDateColumeValue(date,Calendar.HOUR_OF_DAY);
    }
    public static int getMinute(Date date){
        return getDateColumeValue(date,Calendar.MINUTE);
    }

    public static int getSecond(Date date){
        return getDateColumeValue(date,Calendar.SECOND);
    }

	/**
	 * 获取周几
	 * @param date
	 * @return
	 */
	public static String getWeekOfDate(Date date, String[] weekdays) {
        if (weekdays == null || weekdays.length != 7) {
			return null;
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekdays[w];
    }
	
	public static String getWeekOfDate(Date date) {
        return getWeekOfDate(date,WEEKDAYS1);
    }
	
	public static String getWeekOfDate(String[] weeks) {
        return getWeekOfDate(new Date(),weeks);
    }
	
	public static String getWeekOfDate() {
		return getWeekOfDate(new Date());
	}
	
	/**
	 * 目标日期是否在指定日期区间内(闭合区间) 
	 * @param destDate 目标日期
	 * @param startDate  起始日期
	 * @param stopDate  终止日期
     * @param format 格式
	 * @return
	 */
	public static boolean isInPeriodDate(Date destDate, Date startDate, Date stopDate, String format){

	    if (destDate == null || startDate == null || stopDate == null){
	        return false;
        }

        destDate = getFormatDate(destDate,format);
        startDate = getFormatDate(startDate,format);
        stopDate = getFormatDate(stopDate,format);

        if (destDate == null || startDate == null || stopDate == null){
            return false;
        }

        boolean result = false;
		if (destDate.equals(startDate) || destDate.equals(stopDate)) {
            result = true;
		}
		
		if (destDate.after(startDate) && destDate.before(stopDate)) {
            result = true;
		}
        LogUtils.v("util","isInPeriodDate destDate【"+ destDate.toString()+"】,start【"+startDate.toString()+"】,stop【"+stopDate.toString()+"】 ----->"+result);
		return result;
	}
    public static boolean isInPeriodDate(Date destDate, Date startDate, Date stopDate){
	    return isInPeriodDate(destDate,startDate,stopDate,FORMAT_YMDHMS);
    }

    /**
     * 用于将日期转换成Calendar实例
     */
    public static Calendar convert(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 返回指定年数位移后的日期
     */
    public static Date yearOffset(Date date, int offset) {
        return offsetDate(date, Calendar.YEAR, offset);
    }

    /**
     * 返回指定月数位移后的日期
     */
    public static Date monthOffset(Date date, int offset) {
        return offsetDate(date, Calendar.MONTH, offset);
    }

    /**
     * 返回指定天数位移后的日期
     */
    public static Date dayOffset(Date date, int offset) {
        return offsetDate(date, Calendar.DATE, offset);
    }

    /**
     * 返回指定小时数位移后的日期
     *
     * @param date   参考时间
     * @param offset 位移数量，正数表示之前的时间，负数表示之后的时间
     */
    public static Date hourOffset(Date date, int offset) {
        return offsetDate(date, Calendar.HOUR_OF_DAY, offset);
    }

    /**
     * 返回指定日期相应位移后的日期
     *
     * @param date   参考日期
     * @param field  位移单位，见 {@link Calendar}
     * @param offset 位移数量，正数表示之后的时间，负数表示之前的时间
     * @return 位移后的日期
     */
    public static Date offsetDate(Date date, int field, int offset) {
        Calendar calendar = convert(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    /**
     * 返回指定日期对应的0时0分0秒Date对象
     */
    public static Date getDateZero(Date date) {
        Calendar calendar = convert(date);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 返回两个日期间的差异天数
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return 参照日期与比较日期之间的天数差异，正数表示参照日期在比较日期之后，0表示两个日期同天，负数表示参照日期在比较日期之前
     */
    public static int dayDiff(Date date1, Date date2) {
        long diff = getDateZero(date1).getTime() - getDateZero(date2).getTime();
        return (int) (diff / DAY);
    }
}
