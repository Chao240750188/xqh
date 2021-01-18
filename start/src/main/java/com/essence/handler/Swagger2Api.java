package com.essence.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Api {
	@Bean
	public Docket createAllApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("rcs")
				.apiInfo(apiInfo("河长制功能接口","河长制系统，Spring Boot中使用Swagger2构建RESTful APIs","localhost","1.0"))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.essence.rcs"))
				.paths(PathSelectors.any())
				.build();
	}
	@Bean
	public Docket createSysApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("system")
				.apiInfo(apiInfo("系统框架功能接口","河长制系统，Spring Boot中使用Swagger2构建RESTful APIs","localhost","1.0"))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.essence.framework"))
				.paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo(String title, String descp, String url, String version) {
		return new ApiInfoBuilder().title(title).description(descp)
				.termsOfServiceUrl(url).version(version).build();
	}

}
