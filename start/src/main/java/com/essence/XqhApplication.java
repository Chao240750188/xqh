package com.essence;

import com.essence.framework.jpa.EssenceJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.essence"})
@EnableTransactionManagement
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.essence"})
@EnableJpaRepositories(repositoryFactoryBeanClass = EssenceJpaRepositoryFactoryBean.class)
@PropertySource({
        "classpath:application.properties",
        "classpath:druid.properties",
        "classpath:prop/${spring.profiles.active}/conf.properties",
        "classpath:prop/${spring.profiles.active}/db1.properties",
        "classpath:prop/${spring.profiles.active}/tuoying.properties"
})
public class XqhApplication {

    public static void main(String[] args) {
        SpringApplication.run(XqhApplication.class, args);
    }

}
