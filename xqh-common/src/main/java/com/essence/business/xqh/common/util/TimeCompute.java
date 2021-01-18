package com.essence.business.xqh.common.util;

import java.util.Calendar;
import java.util.Date;

public class TimeCompute {

    public static final Integer[] CALENDAR_FIELDS = new Integer[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};

    public static final Date TimeAdd(Date date, Integer calenderField, Integer num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calenderField, num);
        Date resultDate = calendar.getTime();
        return resultDate;
    }

    public static final Date TimeInit(Date resource, Integer calenderField, Integer[] initTemplate) {
        // Integer[] values = new Integer[]{1900,0,1,0,0,0};
        boolean match = false;
        Calendar c = Calendar.getInstance();
        c.setTime(resource);
        Integer[] array = new Integer[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
        for (Integer index = 0; index < array.length; index++) {
            if (array[index] == calenderField) {
                c.set(calenderField, initTemplate[index]);
                match = true;
            } else if (match) {
                c.set(array[index], initTemplate[index]);
            }
        }
        Date targetDate = c.getTime();
        return targetDate;
    }

    /**
     * 时间初始化
     *
     * @param resource      传入的时间参数基础
     * @param calenderField 从哪个时间点开始初始化（年，月，日，时，分，秒，毫秒）
     * @return 初始化的时间，本方法按照(1900年1月1日0点0分0秒作为模板初始化）
     */
    public static final Date TimeBegin(Date resource, Integer calenderField) {
        Integer[] beginTemplate = new Integer[]{1900, 0, 1, 0, 0, 0, 0};
        return TimeInit(resource, calenderField, beginTemplate);
    }

    /**
     * 时间初始化
     * @param time 按照年，月，日...秒，毫秒的顺序填入参数，后面没填的会补时间初始值。(注意月份从0开始)
     *             eg: initTime(2017,2) = 2017-3-1 00:00:00.000
     * @return
     */
    public static final Date initTime(Integer... time) {
        Integer[] beginTemplate = new Integer[]{1900, 0, 1, 0, 0, 0, 0};
        Integer[] array = new Integer[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
        try {
            if(time.length > array.length){
                throw new IllegalArgumentException("参数数量错误，参数数量不得超过7(从年份到毫秒数)");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        Calendar c = Calendar.getInstance();
        for(int i=0;i<array.length;i++){
            if(i<time.length) {
                c.set(array[i], time[i]);
            }else{
                c.set(array[i], beginTemplate[i]);
            }
        }
        return c.getTime();
    }

    /**
     * 时间初始化
     *
     * @param resource      传入的时间参数基础
     * @param calenderField 从哪个时间点开始初始化（年，月，日，时，分，秒，毫秒）
     * @return 初始化的时间，本方法按照(2099年12月31日23点59分59秒作为模板初始化）
     */
    public static final Date TimeEnd(Date resource, Integer calenderField) {
        Integer[] endTemplate = new Integer[]{2099, 11, 31, 23, 59, 59, 999};
        return TimeInit(resource, calenderField, endTemplate);
    }

    /**
     * 时间初始化
     *
     * @param resource 传入的时间参数基础
     * @return 初始化为早上8点
     */
    public static final Date Time8clock(Date resource) {
        Integer[] endTemplate = new Integer[]{0, 0, 0, 8, 0, 0, 0};
        return TimeInit(resource, Calendar.HOUR, endTemplate);
    }

    /**
     * 时间小时数向上取整 (向下取整请用TimeBegin）
     * eg: 8:05:00  ->  9:00:00; 8:00:00 -> 8:00:00
     * @return
     */
    public static final Date UpperDate(Date resource){
        Date beginTime = TimeBegin(resource,Calendar.MINUTE);
        if(beginTime.before(resource)){
            beginTime = TimeAdd(beginTime,Calendar.HOUR,1);
        }
        return beginTime;
    }

    /**
     * 判断传入的时间是否是当前的年，月，日，小时
     * @param time
     * @param calendarField
     * @return
     */
    public static final boolean isCurrentTime(Date time,Integer calendarField) {
        boolean res = false;
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        int cNow_y = c.get(Calendar.YEAR);
        int cNow_m = c.get(Calendar.MONTH);
        int cNow_d = c.get(Calendar.DATE);
        c.setTime(time);
        int cTime_y = c.get(Calendar.YEAR);
        int cTime_m = c.get(Calendar.MONTH);
        int cTime_d = c.get(Calendar.DATE);
        switch (calendarField){
            case Calendar.YEAR:
                res = cNow_y==cTime_y;
                break;
            case Calendar.MONTH:
                res = cNow_y==cTime_y && cNow_m==cTime_m;
                break;
            case Calendar.DATE:
                res = cNow_y==cTime_y && cNow_m==cTime_m && cNow_d==cTime_d;
                break;
            case Calendar.HOUR_OF_DAY:
                res = UpperDate(time).equals(UpperDate(now)) || UpperDate(time).after(UpperDate(now));
                break;
            default:
                return false;
        }
        return res;
    }

    public static final Integer get(Date date,Integer calendarField){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(calendarField);
    }

//    public static void main(String []args){
//        Date d = new Date();
//        CNSimpleDateFormat sdf = new CNSimpleDateFormat(CNSimpleDateFormat.Y_M_D_24H_m_s);
//        try {
//            System.out.println(isCurrentTime(sdf.parse("2018-9-13 17:01:00"),Calendar.DATE));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

}

