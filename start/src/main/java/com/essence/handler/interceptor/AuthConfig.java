package com.essence.handler.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName AuthConfig
 * @Description 认证拦截器
 * @Author zhichao.xing
 * @Date 2019/7/31 14:47
 * @Version 1.0
 **/
@Configuration

public class AuthConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/aaa")
                .excludePathPatterns("/index.html",
                        "/assets/**",
                        "/mock/**",
                        "/js/**",
                        "/images/**",
                        "/rest/**",
                        "/websocket/**",
                        "/AttachFileAction/preview/**",
                        "/AttachFileAction/downLoad/**",
                        "/AttachFileAction/getContent/**",
                       "/caseVolumeAnalysis/**",
                       "/lawEnforcementInspectionc/**",
                       "/licenseApproval/**",
                        "/modelResult/**",
                        "/space/**",
                       "/thirdAction/**"
                       /* "/floodSchedulingMajorUnit/**",
                        "/floodSchedulingRiverMajorUnit/**",
                        "/floodSchedulingFxqxzz/**",
                        "/monitoringActualTimeWaterLevel/**",
                        "/monitoringActualTimeRain/**",
                        "/monitoringForecastRain/**",
                        "/monitoringEquipmentOperate/**",
                        "/sectionInspect/**",
                        "/restHdhzzQuestion/**",
                        "/waterQualityActualTimeMonitor/**",
                        "/waterEnvMonitor/**",
                        "/baseData/**"*/
                        );
    }
}
