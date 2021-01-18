/**   
 * @Title: PropertiesUtil.java
 * @Package: com.zhurong.utils.properties
 * @author LZG, liuzhongguochn@gmail.com  
 * Copyright (c) 2018 https://onezg.cnblogs.com
 */
package com.essence.business.xqh.common.util;

import java.io.*;
import java.util.Properties;

/**
 * @description: Properties工具类
 * @author LZG
 * @date 2018/8/27
 */
public class PropertiesUtil {

    /**
     * @Description: 根据绝对路径文件名获取Properties对象
     * @param fileName
     * @return java.util.Properties
     * @author LZG
     * @date 2018/8/27
     */
    public static Properties readFromAbsolutePath(String fileName) {
        if (null == fileName || "".equals(fileName))
            return null;

        Properties prop = new Properties();
        InputStream in = null;

        try {
            in = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (in == null)
                return null;
            prop.load(in);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @Description: 根据相对路径文件名获取Properties对象
     *      --使用最多。各种情况参考测试用例！
     * @param fileName
     * @return java.util.Properties
     * @author LZG
     * @date 2018/8/27
     */
    public static Properties read(String fileName) {

        if (null == fileName || "".equals(fileName))
            //throw new RuntimeException("fileName不能为空");
            return null;

        InputStream in = null;
        try {
            Properties prop = new Properties();
            in = PropertiesUtil.class.getResourceAsStream(fileName);

            if (in == null)
                return null;
            prop.load(in);

            return prop;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * @Description:  根据键名获取值
     *      -- 也可以直接使用官方提供的properties.getProperty("XXX")
     * @param prop
     * @param key
     * @return java.lang.String
     * @author LZG
     * @date 2018/8/27
     */
    public static String readKeyValue(Properties prop, String key) {
        if (prop != null)
            return prop.getProperty(key);

        return null;
    }

    /*// 测试
    public static void main(String[] args) {
        Properties prop = PropertiesUtil.read("prop/conf.properties");

        // 使用本工具类的readKeyValue方法
        System.out.println(PropertiesUtil.readKeyValue(prop, "mail.subject.register"));
        System.out.println(PropertiesUtil.readKeyValue(prop, "hello")); // properties文件没有的，返回null

        // 使用官方的getProperty方法
        System.out.println(prop.getProperty("mail.username"));
        System.out.println(prop.getProperty("world")); // properties文件没有的，返回null
    }*/

}