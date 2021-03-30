package com.essence.business.xqh.api.systemMonitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器监控-CPU参数监控
 * @author NoBugNoCode
 * @date 2021/3/30 9:37
 */
@Data
public class MemoryMnitorDto implements Serializable {
    /**
     * 内存总量 kb
     */
    private String total;
    /**
     * 当前内存使用量
     */
    private String used;
    /**
     * 当前内存剩余量
     */
    private String free;
    /**
     * 交换区总量
     */
    private String exchangeTotal;
    /**
     * 当前交换区使用量
     */
    private String currentExchangeUsed;
    /**
     * 当前交换区剩余量
     */
    private String currentExchangeFree;

}
