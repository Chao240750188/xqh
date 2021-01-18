package com.essence.handler;

import com.essence.jdbc.JdbcUtil;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class GlobalConfiguration {

	/**
	 * 设置文件上传大小
	 *
	 * @return
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		System.out.println(JdbcUtil.getApplicationContext());
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("10MB");
		factory.setMaxRequestSize("10MB");
		return factory.createMultipartConfig();
	}
}
