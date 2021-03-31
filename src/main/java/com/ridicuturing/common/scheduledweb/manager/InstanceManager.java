package com.ridicuturing.common.scheduledweb.manager;

import com.ridicuturing.common.scheduledweb.bean.Instance;

/**
 * 实例管理器
 * 在应用有多个实例时，获取各实例的情况以实现分布式管理
 *
 * @author chenzhihai
 * @date 2021/02/27
 */
public interface InstanceManager {
    default void init() {
    }

    default Instance[] getInstances() {
        return null;
    }

    default Instance getInstance(String ip, Integer port) {
        return null;
    }


    /**
     * 是否在本实例中运行
     *
     * @param instance
     * @return boolean
     */
    default boolean isPresentInstance(Instance instance) {
        return false;
    }
}
