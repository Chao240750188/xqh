package com.essence.business.xqh.api.systemMonitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器监控-CPU参数监控
 * @author NoBugNoCode
 * @date 2021/3/30 9:37
 */
@Data
public class CpuMnitorDto implements Serializable {
    /**
     * CPU的总量MHz
     */
    private String total;
    /**
     * CPU生产商
     */
    private String cupMake;
    /**
     * CPU类别
     */
    private String cpuVersion;
    /**
     * CPU用户使用率
     */
    private String userUsed;
    /**
     * CPU系统使用率
     */
    private String systemUsed;
    /**
     * CPU当前等待率
     */
    private String waitFor;
    /**
     * CPU当前空闲率
     */
    private String cpuFree;
    /**
     * CPU总的使用率
     */
    private String totalUsed;


}
