package com.essence.business.xqh.common.util;


/**
 * 生成盐码和去盐
 */
public class PwdSalt {
    public static String RemoveSalt(String pwdSalt, Integer salt) {
        StringBuffer result=new StringBuffer();
        String[] pwdChars=pwdSalt.split("_") ;
        for(String e:pwdChars){
            int asc= Integer.parseInt(e);
            result.append((char)(asc^salt));
        }
        return result.toString();
    }
    public static int ramdomSalt(){
        int res=(int)(Math.random()*900)+100;
        return res;
    }

    public static int ramdomFixLengthVerifyCode(){
        int res=(int)(Math.random()*900000)+100000;
        return res;
    }
}
