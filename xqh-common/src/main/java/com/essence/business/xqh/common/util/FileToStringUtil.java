package com.essence.business.xqh.common.util;

import com.essence.framework.util.FileUtil;
import jodd.util.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File to Base64 (文件与字符串相关转换)封装工具
 * 
 * @author lxf
 *
 *         2018年5月11日 上午9:18:52
 */
public class FileToStringUtil {
	 /**
     * 文件转base64字符串
     * @param file
     * @return
     */
	/**
    *
    * @param
    * @return String
    * @description 将文件转base64字符串
    * @date 2018年3月20日
    * @author changyl
    */
   public static String fileToBase64(String filePath) {
	   if (filePath == null) {  
	        return null;  
	    }  
	    try {  
	        byte[] b = Files.readAllBytes(Paths.get(filePath));  
	        return Base64.encodeToString(b); 
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	  
	    return null;  
   }
    /**
     * base64字符串转文件
     * @param base64
     * @return
     */
   public static File base64ToFile(String base64, String fileName) {
       File file = null;
       System.out.println("调用base64转换……");
       //创建文件目录
       String filePath= FileUtil.getRootLocation()+"/remoteHdhzz/";
       File  dir=new File(filePath);
       if (!dir.exists() && !dir.isDirectory()) {
               dir.mkdirs();
       }
       BufferedOutputStream bos = null;
       java.io.FileOutputStream fos = null;
       try {
           byte[] bytes = Base64.decode(base64);
           file=new File(filePath+"\\"+fileName);
           fos = new java.io.FileOutputStream(file);
           bos = new BufferedOutputStream(fos);
           bos.write(bytes);
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           if (bos != null) {
               try {
                   bos.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           if (fos != null) {
               try {
                   fos.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       return file;
   }
   
   public static void main(String[] args) {
	String path="E:/市级下达/接口定义.txt";
	System.out.println(fileToBase64(path));
}
}
