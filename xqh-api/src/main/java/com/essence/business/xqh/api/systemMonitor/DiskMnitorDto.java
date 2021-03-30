package com.essence.business.xqh.api.systemMonitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器监控-磁盘参数监控
 * @author NoBugNoCode
 * @date 2021/3/30 9:37
 */
@Data
public class DiskMnitorDto implements Serializable {
    /**
     * 盘符名称
     */
    private String name;
    /**
     * 盘符路径
     */
    private String path;
    /**
     * 盘符类型
     */
    private String type;
    /**
     * 盘符类型名
     */
    private String local;
    /**
     * 总大小
     */
    private String total;
    /**
     * 剩余大小
     */
    private String free;

    /**
     * 可用大小
     */
    private String available;

    /**
     * 资源的利用率
     */
    private String resourcesUsed;

    /**
     * 读出
     */
    private String output;

    /**
     * 写入
     */
    private String input;

}
