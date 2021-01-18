package com.essence.jdbc;

import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 不用注入，自己手动获取数据操作对象 2.获取spring的bean
 * 在使用前必须前初始化,init()
 *
 * @author Gavin
 * @version 1.0 Gavin 2018年5月18日 下午8:36:05
 * @title JdbcUtil.java
 * @since 2018年5月18日 下午8:36:05
 */
public class JdbcUtil {
    static{
        System.out.println(JdbcUtil.class.getClassLoader());
    }
    private static JdbcUtil instance = new JdbcUtil();
    private String databaseProductName;
    private String databaseProductVersion;
    private ApplicationContext ctx;

    private JdbcUtil() {
//        System.out.println("***************************************");
//        System.out.println(ThreadCallStackUtil.printThreadCallStack());
//        System.out.println("***************************************");
//        System.out.println();
//        System.out.println("JDBCUtil has bean create :" + (++count) + " times");
    }

    public static JdbcUtil getInstance() {
        return instance;
    }

    public static ApplicationContext getApplicationContext() {

        ApplicationContext c = getInstance().ctx;
        if (c == null) {
            System.err.println("JDBCUtil初始化失败");
        }
        return c;
    }

    public static synchronized void init(ApplicationContext context) {
        getInstance().ctx = context;

        try {
            instance.databaseProductName = getDataSource().getConnection().getMetaData().getDatabaseProductName();
            if (instance.databaseProductName != null) {
                instance.databaseProductName = instance.databaseProductName.toLowerCase();
            }
            instance.databaseProductVersion = getDataSource().getConnection().getMetaData().getDatabaseProductVersion();
        } catch (SQLException e) {
            System.err.println("jdbcUtil初始化失败");
            e.printStackTrace();
        }
    }

    /**
     * 根据名称获取bean
     *
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId) {
        return getApplicationContext().getBean(beanId);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 根据名称获取bean
     *
     * @param beanId
     * @return
     */
    public static Object getBean(String context, String beanId) {
        return getApplicationContext().getBean(beanId);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(String context, Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 获取数据库连接工具
     *
     * @return
     */
    public static DefaultJdbcOperations getJdbcOperator() {
        return getBean(DefaultJdbcOperations.class);
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public static DataSource getDataSource() {
        try {
            return getBean(DataSource.class);
        } catch (Exception e) {
            return (DataSource) getBean("dataSource");
        }
    }


    /**
     * 获取数据库类型，小写:oracle、mysql
     */
    public static String getDatabaseProductName() {
        String d = getInstance().databaseProductName;
        if (d == null) {
            System.err.println("jdbcUtil未初始化");
        }
        return d;
    }

    /**
     * 获取数据库版本
     */
    public static String getDatabaseProductVersion() {
        String d = getInstance().databaseProductVersion;
        if (d == null) {
            System.err.println("jdbcUtil未初始化");
        }
        return d;
    }
}
