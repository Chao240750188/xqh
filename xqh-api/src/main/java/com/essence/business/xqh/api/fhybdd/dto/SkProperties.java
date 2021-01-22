package com.essence.business.xqh.api.fhybdd.dto;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "skdd")
@Component
@PropertySource("classpath:filePath.properties")
public class SkProperties {

    private Map<String,String> ID_NAME;

    public Map<String, String> getID_NAME() {
        return ID_NAME;
    }

    public void setID_NAME(Map<String, String> ID_NAME) {
        this.ID_NAME = ID_NAME;
    }
}
