package com.essence.business.xqh.common.util.gis;

public class RainFallLevelTz {
    public static final Double[] LEVEL_1 = {0.0, 2.5, 8.0, 16.0, 20.0, 20.0};
    public static final Double[] LEVEL_6 = {0.0, 4.0, 12.0, 25.0, 55.0, 90.0};
    public static final Double[] LEVEL_12 = {0.0, 5.0, 15.0, 30.0, 70.0, 140.0};
    public static final Double[] LEVEL_24 = {0.0, 10.0, 25.0, 50.0, 100.0, 200.0};

    public static Double[] getLevel(Integer hours) {
        if (hours.compareTo(1) == 0) {
            return LEVEL_1;
        } else if (hours.compareTo(6) <= 0) {
            return LEVEL_6;
        } else if (hours.compareTo(12) <= 0) {
            return LEVEL_12;
        } else {
            return LEVEL_24;
        }
    }

    /**
     * 根据降雨量评定等级
     *
     * @param rainfall
     * @return 降雨等级  0 无雨， 1 小雨  2 中雨  3 大雨  4 暴雨  5 大暴雨  6 特大暴雨
     */
    public static Integer getRainFallLevel(Double rainfall, Double[] hourLevel) {
        if(hourLevel[1].compareTo(2.5)==0){
            if (rainfall.compareTo(hourLevel[0]) == 0)
                return 0;
            else if (rainfall.compareTo(hourLevel[1]) < 0)
                return 1;
            else if (rainfall.compareTo(hourLevel[2]) < 0)
                return 2;
            else if (rainfall.compareTo(hourLevel[2])>=0 && rainfall.compareTo(hourLevel[3])<=0)
                return 3;
            else if (rainfall.compareTo(hourLevel[5]) > 0)
                return 5;
            else
                return 4;
        }
        if (rainfall.compareTo(hourLevel[0]) == 0)
            return 0;
        else if (rainfall.compareTo(hourLevel[1]) < 0)
            return 1;
        else if (rainfall.compareTo(hourLevel[2]) < 0)
            return 2;
        else if (rainfall.compareTo(hourLevel[3]) < 0)
            return 3;
        else if (rainfall.compareTo(hourLevel[4]) < 0)
            return 4;
        else if (rainfall.compareTo(hourLevel[5]) < 0)
            return 5;
        else
            return 6;
    }
}
