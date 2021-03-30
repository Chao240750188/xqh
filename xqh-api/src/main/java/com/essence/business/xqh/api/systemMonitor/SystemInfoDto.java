package com.essence.business.xqh.api.systemMonitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器监控-系统参数监控
 * @author NoBugNoCode
 * @date 2021/3/30 9:37
 */
@Data
public class SystemInfoDto implements Serializable {
    /**
     * 系统用户名
     */
    private String computerUser;
    /**
     * 计算机名
     */
    private String computerName;
    /**
     * 计算机域名
     */
    private String computerDomainName;
    /**
     * 本地(内网络)ip地址
     */
    private String ip;
    /**
     * 本地主机名
     */
    private String computerLocalName;
    /**
     * JVM可以使用的总内存
     */
    private String jvmTotal;
    /**
     * JVM可以使用的剩余内存
     */
    private String jvmFree;
    /**
     * JVM可以使用的处理器个数
     */
    private String cpuCounts;
    /**
     * Java的运行环境版本
     */
    private String jvmEdition;
    /**
     * 操作系统的名称
     */
    private String system;
    /**
     * 操作系统的构架
     */
    private String systemByte;
    /**
     * 操作系统的版本
     */
    private String systemEdition;

}
