package com.essence.business.xqh.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @ClassName DateUtil
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2019/10/18 11:22
 * @Version 1.0
 **/
public class LocalDateUtils {

    public static Date transferLocalDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }
    /**
     * Date转换为LocalDateTime
     * @param date
     */
    public static LocalDateTime date2LocalDateTime(Date date){
        Instant instant = date.toInstant();//An instantaneous point on the time-line.(时间线上的一个瞬时点。)
        ZoneId zoneId = ZoneId.systemDefault();//A time-zone ID, such as {@code Europe/Paris}.(时区)
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
return localDateTime;
//        System.out.println(localDateTime.toString());//2018-03-27T14:07:32.668
//        System.out.println(localDateTime.toLocalDate() + " " +localDateTime.toLocalTime());//2018-03-27 14:48:57.453
//
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//This class is immutable and thread-safe.@since 1.8
//        System.out.println();//2018-03-27 14:52:57
//        String format = dateTimeFormatter.format(localDateTime);
    }

}
