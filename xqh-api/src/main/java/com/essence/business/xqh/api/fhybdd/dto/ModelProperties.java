package com.essence.business.xqh.api.fhybdd.dto;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "swyb")
@Component
@PropertySource("classpath:filePath.properties")
public class ModelProperties {

    private Map<String,String> model;

    public Map<String, String> getModel() {
        return model;
    }

    public void setModel(Map<String, String> model) {
        this.model = model;
    }
}
