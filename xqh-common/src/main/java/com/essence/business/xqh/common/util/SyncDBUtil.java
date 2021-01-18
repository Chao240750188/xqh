package com.essence.business.xqh.common.util;

import com.essence.framework.util.PropertiesUtil;

import java.sql.*;
import java.util.Properties;

public class SyncDBUtil {

    // 数据库连接地址
    public static String URL;
    // 用户名
    public static String USERNAME;
    // 密码
    public static String PASSWORD;
    // mysql的驱动类
    public static String DRIVER;

    private static Properties prop = PropertiesUtil.getCofigProperties("/prop/test/tuoying.properties");

    private static Connection conn;

    // 使用静态块加载驱动程序
    static {
        URL = prop.getProperty("dynamic.datasource.tuoying.url");
        USERNAME = prop.getProperty("dynamic.datasource.tuoying.username");
        PASSWORD = prop.getProperty("dynamic.datasource.tuoying.password");
        DRIVER = prop.getProperty("dynamic.datasource.tuoying.driverClassName");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 定义一个获取数据库连接的方法
    public static Connection getConnection() {
        if (conn!=null){
            try{
                if (conn.isValid(0)){
                    return conn;
                }
            } catch (Exception e){
                try {
                    conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("获取连接失败");
                }
                return conn;
            }
        }
        /*System.out.println(URL);
        System.out.println(USERNAME);
        System.out.println(PASSWORD);*/
        //Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("获取连接失败");
        }
        return conn;
    }

    // 关闭数据库连接
    public static void close(ResultSet rs, Statement stat, Connection conn) {
        try {
            if (rs != null)
                rs.close();
            if (stat != null)
                stat.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
