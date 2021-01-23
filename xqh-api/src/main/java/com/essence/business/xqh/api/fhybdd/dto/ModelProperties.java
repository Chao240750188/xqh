package com.essence.business.xqh.api.fhybdd.dto;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "swyb")
@Component
@PropertySource("classpath:filePath.properties")
public class ModelProperties {

    private Map<String,String> modelMap;

    public Map<String, String> getModelMap() {
        return modelMap;
    }

    public void setModelMap(Map<String, String> modelMap) {
        this.modelMap = modelMap;
    }
}
