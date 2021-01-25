package com.essence.business.xqh.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 日期处理工具类
 * @title DateUtil
 * @author Gavin
 * @since 2012/12/18
 * @version 1.0
 * @version 2.0 2016/5/18 Gavin 新增带参数的获取当天到当年的时间
 * @version 2.1 2017/12/22 Gavin 新增将日期类型转换为日期字符串,自动匹配日期格式，效率较低
 * @version 2.2 2018/1/13 Gavin 新增获取当前季度时间
 */
public class DateUtil {
	
	public static final int MILLISECOND=Calendar.MILLISECOND;
	public static final int SECOND=Calendar.SECOND;
	public static final int MINUTE=Calendar.MINUTE;
	public static final int HOUR=Calendar.HOUR_OF_DAY;
	public static final int DAY=Calendar.DAY_OF_MONTH;
	public static final int MONTH=Calendar.MONTH;
	public static final int YEAR=Calendar.YEAR;
	public static final int DAY_OF_YEAR=Calendar.DAY_OF_YEAR;
	public static final int DAY_OF_WEEK=Calendar.DAY_OF_WEEK;
	
	private DateUtil(){};


	/**
	 * 当前指定日期所属年的开始时间
	 *
	 * @return
	 */
	public static Date getYearStartTime(Date date) {
		//将Date转换为LocalDateTime
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR,localDateTime.getYear());
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);

		return c.getTime();
	}

	/**
	 * 获取某年最后一天日期
	 *
	 *
	 * @return Date
	 */
	public static Date getYearLast(Date date) {
        //将Date转换为LocalDateTime
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, localDateTime.getYear());
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}


	/**
	 * 查询指定日期所在年份的所有日
	 * @Author huangxiaoli
	 * @Description
	 * @Date 17:30 2020/8/27
	 * @Param [date]
	 * @return java.util.List<java.util.Date>
	 **/
	public static List<Date> getSearchMonthDay(Integer monNum){

		List<Date> list = new ArrayList<>();

		//获取当前月的第一天
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH,monNum);
		cal.set(Calendar.DAY_OF_MONTH,1);
		Date startTime = cal.getTime();


		//获取当前月的天数
		int actualMaximum = cal.getActualMaximum(Calendar.DATE);
		for (int i=0;i<actualMaximum;i++){
			Date date = new Date(startTime.getTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DATE,i);
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
			list.add(calendar.getTime());
		}

		return list;
	}



	/**
	 * 获取雨量当前月的第一天(从8点开始)
	 * @Author huangxiaoli
	 * @Description
	 * @Date 10:55 2020/8/28
	 * @Param []
	 * @return java.util.Date
	 **/
	public  static Date getRainMonthFirstDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY,8);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		Date startTime = cal.getTime();
		return startTime;
	}

	/**
	 * 获取雨量当前月的最后一天（为下月1号8点）
	 * @Author huangxiaoli
	 * @Description
	 * @Date 10:55 2020/8/28
	 * @Param []
	 * @return java.util.Date
	 **/
	public  static Date getRainMonthEndDay(Date date){
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.MONTH,1);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		ca.set(Calendar.HOUR_OF_DAY,8);
		ca.set(Calendar.MINUTE,0);
		ca.set(Calendar.SECOND,0);
		Date endTime = ca.getTime();
		return endTime;
	}



	/**
	 * 获取当前月的第一天
	 * @Author huangxiaoli
	 * @Description
	 * @Date 10:55 2020/8/28
	 * @Param []
	 * @return java.util.Date
	 **/
	public  static Date getMonthFirstDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		Date startTime = cal.getTime();
		return startTime;
	}

	/**
	 * 获取当前月的最后一天
	 * @Author huangxiaoli
	 * @Description
	 * @Date 10:55 2020/8/28
	 * @Param []
	 * @return java.util.Date
	 **/
	public  static Date getMonthEndDay(Date date){
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		ca.set(Calendar.HOUR_OF_DAY,23);
		ca.set(Calendar.MINUTE,59);
		ca.set(Calendar.SECOND,59);
		Date endTime = ca.getTime();
		return endTime;
	}




	/**
	 * 获取当前月的第一天
	 * @Author huangxiaoli
	 * @Description
	 * @Date 10:55 2020/8/28
	 * @Param []
	 * @return java.util.Date
	 **/
	public  static Date getCurrentMonthFirstDay(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		Date startTime = cal.getTime();
		return startTime;
	}

	/**
	 * 获取当前月的最后一天
	 * @Author huangxiaoli
	 * @Description
	 * @Date 10:55 2020/8/28
	 * @Param []
	 * @return java.util.Date
	 **/
	public  static Date getCurrentMonthEndDay(){
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.MONTH,0);
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		ca.set(Calendar.HOUR_OF_DAY,23);
		ca.set(Calendar.MINUTE,59);
		ca.set(Calendar.SECOND,59);
		Date endTime = ca.getTime();
		return endTime;
	}

	/**
	 * 根据年 月 获取对应的月份 天数
	 * @Author huangxiaoli
	 * @Description
	 * @Date 15:14 2020/10/28
	 * @Param [year, month]
	 * @return int
	 **/
    public static int getDaysByYearMonth(int year,int month){
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}


	/**
	 * 获取时间time的前后若干年的时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的年数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextYear(Date date,int step){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(YEAR, step);//加时
		return cal.getTime();
	}
	/**
	 * 获取时间time的前后若干月的时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的月数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextMonth(Date date,int step){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(MONTH, step);//加时
		return cal.getTime();
	}
	/**
	 * 获取时间time的前后若干天的时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的天数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextDay(Date date,int step){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(DAY, step);
		return cal.getTime();
	}
	
	/**
	 * 获取时间time的前后若干的小时的时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的小时数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextHour(Date date,int step){
		  Calendar cal=Calendar.getInstance();
		  cal.setTime(date);
		  cal.add(HOUR, step);//加时
		  return cal.getTime();
	}
	
	
	/**
	 * 获取时间time的前后若干的分钟的时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的分钟数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextMinute(Date date,int step){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(MINUTE, step);//加时
		return cal.getTime();
	}
	
	/**
	 * 获取时间time的前后若干的秒时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的秒数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextSecond(Date date,int step){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(SECOND, step);//加时
		return cal.getTime();
	}
	
	/**
	 * 获取时间time的前后若干的毫秒的时间
	 * @param date Date类型，基础时间
	 * @param step int类型，要向前或身后的毫秒数
	 * @return backDate Date类型，返回的时间
	 */
	public static Date getNextMillis(Date date,int step){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(MILLISECOND, step);//加时
		return cal.getTime();
	}
	
	
	/**
	 * 根据时间字符串返回日期类型,time的格式为yyyy/MM/dd HH:mm:ss
	 * @param time String类型，时间字符串
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateByStringNormal(String time){
		 SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  Date date=null;
		  try {
			date=df.parse(time);
			 return date;
		  } catch (ParseException e) {
			  return null;
		  }
	}
	
	/**
	 * 根据时间字符串返回日期类型,time的格式为yyyy/MM/dd'T'HH:mm:ss
	 * @param time String类型，时间字符串
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateByStringNormalT(String time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
		Date date=null;
		try {
			date=df.parse(time);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 根据时间字符串返回日期类型,time的格式为yyyy/MM/dd
	 * @param time String类型，时间字符串
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateByStringDay(String time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date=null;
		try {
			date=df.parse(time);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	 * 根据时间字符串返回日期类型,time的格式为yyyy/MM/dd
	 * @param time String类型，时间字符串
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateByStringDay2(String time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date date=null;
		try {
			date=df.parse(time);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	 * 根据时间字符串返回日期类型,time的格式为yyyy-MM
	 * @param time String类型，时间字符串
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateByStringMonth(String time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		Date date=null;
		try {
			date=df.parse(time);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	/**
	 * 根据时间字符串返回日期类型,time的格式为yyyy/MM/dd HH:mm:ss.SSS
	 * @param time String类型，时间字符串
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateByStringMillis(String time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		Date date=null;
		try {
			date=df.parse(time);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	/**
	 * 根据时间字符串返回日期类型,time的格式为format的定义
	 * @param time String类型，时间字符串
	 * @param format String类型，时间格式
	 * @return date Date类型，返回的时间
	 */
	public static Date getDateWithFormat(String time,String format){
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date=null;
		try {
			date=df.parse(time);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 将日期类型转换为日期字符串,自动匹配日期格式，效率较低
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToString(Date date){
		 SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		 try {
			return df.format(date);
		} catch (Exception e) {
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			 try {
				return df2.format(date);
			} catch (Exception e2) {
				SimpleDateFormat df3 = new SimpleDateFormat("yyyy/MM/dd");
				 try {
					return df3.format(date);
				} catch (Exception e3) {
					SimpleDateFormat df4 = new SimpleDateFormat("yyyy-MM");
					 try {
						return df4.format(date);
					} catch (Exception e4) {
						return date.toString();
					}
				}
			}
		}
	}
	
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM/dd
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringDay(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		return df.format(date);
	}
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM/dd
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringDay2(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}
	
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringYear(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		return df.format(date);
	}
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringMonth(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM");
		return df.format(date);
	}
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringMonth2(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		return df.format(date);
	}

	/**
	 * 将日期类型转换为日期字符串,无格式，默认：/MM
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static Integer dateToStringDateMonth(Date date){
		Calendar cal=Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}
	
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM/dd HH:mm:ss
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringNormal(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return df.format(date);
	}

	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM/dd HH:mm:ss
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringNormal3(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM/dd HH:mm:ss
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringNormal2(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		return df.format(date);
	}
	
	/**
	 * 将日期类型转换为日期字符串,无格式，默认：yyyy/MM/dd HH:mm:ss.SSS
	 * @param date Date类型
	 * @return String类型，日期字符串
	 */
	public static String dateToStringMillis(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		return df.format(date);
	}
	
	
	/**
	 * 将日期类型转换为日期字符串,自定义格式
	 * @param date Date类型
	 * @param format String类型，日期格式化格式
	 * @return String类型，日期字符串
	 */
	public static String dateToStringWithFormat(Date date,String format){
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	
	/**
	 * 两个日期类型相差的分钟数，取天花板数
	 * @param date1 Date类型，时间1
	 * @param date2 Date类型，时间2
	 * @return date2-date1的分钟数,int类型.因为int类型最大值够用4000年以上
	 */
	public static int dValueOfTime(Date date1,Date date2){
		return (int)(Math.ceil((date2.getTime()-date1.getTime())/60000));
	}
	
	/**
	 * 两个日期类型相差的毫秒数
	 * @param date1 Date类型，时间1
	 * @param date2 Date类型，时间2
	 * @return date2-date1的毫秒数
	 */
	public static long dValueOfTimeMillis(Date date1,Date date2){
		return date2.getTime()-date1.getTime();
	}
	
	
	/**
	 * 时间的分钟数（秒被截掉）
	 * @param date Date类型，时间
	 * @return date的分钟数,int类型
	 */
	public static int minuteOfTime(Date date){
		return (int)(date.getTime()/60000);
	}
	
	
	/**
	 * 分钟数转换为日期类型
	 * @param minute int 分钟数
	 * @return 日期
	 */
	public static Date getDateByMinute(int minute){
		Calendar cal=Calendar.getInstance();
		long millis=60000;
		cal.setTimeInMillis(millis*minute);
		return cal.getTime();
	}
	
	/**
	 * 毫秒转换为日期类型
	 * @param millis long 毫秒数
	 * @return 日期
	 */
	public static Date getDateByMillis(long millis){
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return cal.getTime();
	}
	
	/**
	 * 获得时间的一个位置值
	 * @param date
	 * @param field
	 * @return
	 */
	public static int getFiled(Date date,int field){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal.get(field);
	}
	/**
	 * 设置时间的一个域为另一个时间
	 * @param date 原始时间
	 * @param filed 时间域
	 * @param value 时间值
	 * @return 新时间
	 */
	public static Date setFiled(Date date,int field,int value){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.set(field, value);
		return cal.getTime();
	}
	
	/**
	 * 获得当年的起点时间
	 */
	public static Date getThisYear(){
		return getThisYear(getCurrentTime());
	}
	
	/**
	 * 获得设置日期的当年的起点时间
	 */
	public static Date getThisYear(Date date){
		Date month0=getThisMonth(date);
		month0=setFiled(month0,MONTH, 0);
		return month0;
	}
	/**
	 * 获得当季的起点时间
	 */
	public static Date getThisQuarter(){
		return getThisQuarter(getCurrentTime());
	}
	/**
	 * 获得设置日期的当季的起点时间
	 */
	public static Date getThisQuarter(Date date){
		Date quarter=getThisMonth(date);
		int month=getFiled(quarter, com.essence.framework.util.DateUtil.MONTH);
		if(month<3){
			quarter=setFiled(quarter,MONTH, 0);
		}else if(month>=3&&month<6){
			quarter=setFiled(quarter,MONTH, 3);
		}else if(month>=6&&month<9){
			quarter=setFiled(quarter,MONTH, 6);
		}else{
			quarter=setFiled(quarter,MONTH, 9);
		}
		return quarter;
	}
	/**
	 * 获得当月的起点时间
	 */
	public static Date getThisMonth(){
		return getThisMonth(getCurrentTime());
	}
	/**
	 * 获得设置日期的当月的起点时间
	 */
	public static Date getThisMonth(Date date){
		Date day0=getThisDay(date);
		day0=setFiled(day0,DAY, 1);
		return day0;
	}
	
	/**
	 * 获得当前星期的起点时间（周日零点）
	 */
	public static Date getThisWeek(){
		return getThisWeek(getCurrentTime());
	}
	
	/**
	 * 获得设置日期所有星期的起点时间（周日零点）
	 */
	public static Date getThisWeek(Date date){
		int week=getFiled(date, DAY_OF_WEEK);
		Date sunday=getNextDay(date, -(week-1));
		return getThisDay(sunday);
	}
	
	/**
	 * 获得当日的起点时间
	 */
	public static Date getThisDay(){
		return getThisDay(getCurrentTime());
	}
	/**
	 * 获得设置日期的当天起点时间
	 */
	public static Date getThisDay(Date date){
		Date hour0=getThisHour(date);
		hour0=setFiled(hour0, HOUR, 0);
		return hour0;
	}
	/**
	 * 获得当前小时的起点时间
	 */
	public static Date getThisHour(){
		return getThisHour(getCurrentTime());
	}
	
	/**
	 * 获得设置日期的当前小时的起点时间
	 */
	public static Date getThisHour(Date date){
		date=setFiled(date, MILLISECOND, 0);
		date=setFiled(date, SECOND, 0);
		date=setFiled(date, MINUTE, 0);
		return date;
	}
	
	/**
	 * 获得当前时间
	 */
	public static Date getCurrentTime(){
		return Calendar.getInstance().getTime();
	}

	/**
	 * 判断是否润年
	 * @param ddate
	 * @return
	 */
	public static boolean isLeapYear(Date ddate) {
		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(ddate);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0) {
			return true;
		}else if ((year % 4) == 0) {
			if ((year % 100) == 0){
				return false;
			}else{
				return true;
			}
		} else{
			return false;
		}
	}
}
