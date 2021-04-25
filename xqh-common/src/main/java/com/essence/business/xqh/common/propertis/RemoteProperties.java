package com.essence.business.xqh.common.propertis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ClassName RemoteProperties
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2019/10/29 10:47
 * @Version 1.0
 **/
@PropertySource({
        "classpath:prop/${spring.profiles.active}/conf.properties"
})
@Configuration
@ConfigurationProperties(prefix = "remote", ignoreUnknownFields = false)
@Component
@Data
public class RemoteProperties {
    //雨晴等值面
    private String yqdzm;
    //空间信息查询
    private String vecindexintegrate;
    private String identi;

}
