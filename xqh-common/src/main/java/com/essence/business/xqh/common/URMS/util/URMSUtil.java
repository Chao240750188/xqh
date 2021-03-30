package com.essence.business.xqh.common.URMS.util;


import com.essence.business.xqh.common.URMS.pojo.UserInfo;
import com.essence.euauth.common.SysConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class URMSUtil {

    public URMSUtil() {
    }

    /**
     * 获取euauth登陆信息
     *
     * @return
     */
    public static UserInfo getCurrentUserInfo() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute(SysConstant.CURRENT_USER_ID);
        String loginName = (String) session.getAttribute(SysConstant.CURRENT_LOGIN_NAME);
        String userName = (String) session.getAttribute(SysConstant.CURRENT_USERNAME);
        String corpName = (String) session.getAttribute(SysConstant.CURRENT_UNIT_NAME);
        String corpId = (String) session.getAttribute("corpId");
        return new UserInfo(userId, loginName, userName, corpName, corpId);
    }

    public static Object dynCreateClassInstanceByName(String className) {
        Object o = null;

        try {
            Class<?> c = Class.forName(className);
            o = c.newInstance();
        } catch (Exception var4) {
            System.out.println("URMS动态加载类异常" + var4.getMessage());
        }

        return o;
    }


}
