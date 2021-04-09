package com.essence.business.xqh.web.dictionary;

import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.Map;

/**
 * 数据源配置
 *
 * @author NoBugNoCode
 * @date 2021/3/31 11:05
 */
@RestController
@RequestMapping("/dataSource")
public class DataSourceController {

    /**
     * 测试数据源链接
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/testDataSourceConnect", method = RequestMethod.POST)
    public SystemSecurityMessage getDictionaryList(@RequestBody Map<String, String> map) throws Exception {
        String ip = map.get("ip");
        String port = map.get("port");
        String orcl = map.get("orcl");
        String dataBase = map.get("dataBase");
        String password = map.get("password");

        //创建test类，保证文件名与类名相同
        Connection con = null;
        //声明Connection对象
        Statement sql;
        ResultSet res;
        try {
            //加载数据库驱动类
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String url = "jdbc:oracle:thin:@"+ip+":"+port+":"+orcl;
            //通过访问数据库的URL获取数据库连接对象 ，这里后两个参数分别是数据库的用户名及密码
            con = DriverManager.getConnection(url,dataBase,password);
            return new SystemSecurityMessage("ok", "测试成功！", "数据库链接成功！");
        } catch (SQLException e) {
            return new SystemSecurityMessage("error", "测试失败！", "数据库链接信息错误或用户名密码错误！");
        }finally {
            if(con!=null)
                con.close();
        }
    }

    /**
     * 数据源配置保存
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveDataSourceConnect", method = RequestMethod.GET)
    public SystemSecurityMessage saveDataSourceConnect() throws Exception {
            return new SystemSecurityMessage("ok", "数据源配置成功！", "数据源配置更新成功！");
    }
}