package com.essence.prop.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(
        urlPatterns = {"/classpath:prop/test/db1.properties"},
        initParams = {
                @WebInitParam(name = "loginUsername", value = "xqh"),
                @WebInitParam(name = "loginPassword", value = "xqh_db"),
                @WebInitParam(name = "resetEnable", value = "false")
//      @WebInitParam(name = "allow", value = "127.0.0.1")
        }
)
public class WebStatViewServlet extends StatViewServlet {
}
