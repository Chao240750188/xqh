package com.essence.jdbc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * 解决ojdbc在项目停止时无法自动注销的BUG
 *
 * @author Gavin
 */
public class JdbcDriverUnregisterListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
    }

}
