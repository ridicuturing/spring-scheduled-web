package com.ridicuturing.common.scheduledweb.bean;

import lombok.Data;

/**
 * 对应注册中心的单个实例，用于选择让某一个实例或广播进行task任务
 *
 * @author chenzhihai
 * @date 2021/02/24
 */
@Data
public class Instance {
    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 是否有效
     */
    private Boolean valid;
    /**
     * 是否健康
     */
    private Boolean healthy;
    /**
     * 是否开启
     */
    private Boolean enabled;

    public Instance(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 基于ip和端口生成hash code
     *
     * @return int
     */
    @Override
    public int hashCode() {
        int hashCode = port;
        if (ip != null) {
            hashCode += ip.hashCode();
        }
        return hashCode;
    }

    /**
     * 两个实例是否相同，只与ip和端口有关
     *
     * @param o o
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Instance) || o.hashCode() != hashCode()) {
            return false;
        }
        Instance var = (Instance) o;
        return ip != null && ip.equals(var.ip) && port != null && port.equals(var.port);
    }

    /**
     * 广播字符串
     */
    public static final String BOARDCAST = "broadcast";

    public static final Instance BROADCAST_INSTANCE = new Instance(BOARDCAST, 0);
}
