package com.essence.business.xqh.api.systemMonitor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务器监控-参数
 * @author NoBugNoCode
 * @date 2021/3/30 9:37
 */
@Data
public class AllMnitorDto implements Serializable {

    /**
     * 当前总cpu的空闲率
     */
    private String cpuFree;
    /**
     * 获取当前总cpu的占用率
     */
    private String cpuUsed;

    /**
     * 系统信息监控
     */
    private SystemInfoDto SystemInfo;

    /**
     * 磁盘信息监控（各磁盘）
     */
    private List<DiskMnitorDto> diskMnitor;

    /**
     * 总内存监控
     */
    private MemoryMnitorDto  memoryMnitor;

    /**
     * cpu块监控（各核处理器监控）
     */
    List<CpuMnitorDto> cpuList;


}
