package com.essence.handler;//package com.essence.handler;
//
//import javax.sql.DataSource;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.support.http.StatViewServlet;
//
//@Configuration
//@PropertySource(value = "classpath:druid.properties")
//public class DruidConfiguration {
//	@Bean(destroyMethod = "close", initMethod = "init")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DruidDataSource druidDataSource() {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        return druidDataSource;
//    }
//	 /**
//     * 注册一个StatViewServlet
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet(){
//        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
//        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<StatViewServlet>(new StatViewServlet(),"/druid/*");
//        //添加初始化参数：initParams
//        //白名单：
//        servletRegistrationBean.addInitParameter("allow","127.0.0.1");
//        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
//        //登录查看信息的账号密码.
//        servletRegistrationBean.addInitParameter("loginUsername","admin");
//        servletRegistrationBean.addInitParameter("loginPassword","essence_admin");
//        //是否能够重置数据.
//        servletRegistrationBean.addInitParameter("resetEnable","false");
//        return servletRegistrationBean;
//    }
//}
