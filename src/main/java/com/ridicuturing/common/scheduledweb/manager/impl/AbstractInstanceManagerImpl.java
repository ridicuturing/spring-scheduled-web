package com.ridicuturing.common.scheduledweb.manager.impl;

import com.ridicuturing.common.scheduledweb.bean.Instance;
import com.ridicuturing.common.scheduledweb.manager.InstanceManager;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 抽象实例管理器
 *
 * @author chenzhihai
 * @date 2021/02/24
 */
public abstract class AbstractInstanceManagerImpl implements InstanceManager {


    /**
     * 实例列表缓存
     */
    protected LinkedHashSet<Instance> instancesCache = new LinkedHashSet<>();

    /**
     * 实例列表缓存最后加载时间
     */
    protected long lastLoadTime = 0;

    /**
     * 实例过期时间（毫秒）
     */
    protected int ttl = 5 * 1000;

    @Value("${server.port:8080}")
    public int port;

    public static String LOCAL_IP;

    static {
        try {
            LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LOCAL_IP = "";
        }
    }

    @Override
    public boolean isPresentInstance(Instance instance) {
        return instance.getIp() != null && instance.getIp().equals(LOCAL_IP) && instance.getPort() != null && instance.getPort().equals(port);
    }

    /**
     * 获得注册中心的所有实例
     * 结果会缓存五秒
     *
     * @return {@link List<Instance>}
     */
    @Override
    public Instance[] getInstances() {
        if (!isCacheExpired() && instancesCache != null) {
            return instancesCache.toArray(new Instance[0]);
        }
        return loadAndGetInstance();
    }

    public boolean isCacheExpired() {
        return System.currentTimeMillis() - lastLoadTime > ttl;
    }

    /**
     * 获取实时的实例组信息，加载到 instancesCache 缓存，并返回该实例组
     *
     * @return {@link List<Instance>}
     */
    protected Instance[] loadAndGetInstance() {
        return null;
    }

    ;

    @Override
    public Instance getInstance(String ip, Integer port) {
        Instance[] instances = getInstances();
        for (Instance instance : instances) {
            if (ip != null && ip.equals(instance.getIp()) && port != null && port.equals(instance.getPort())) {
                return instance;
            }
        }
        return null;
    }

}
