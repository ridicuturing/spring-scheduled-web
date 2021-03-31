package com.ridicuturing.common.scheduledweb.manager.impl;

import com.alibaba.fastjson.JSONObject;
import com.ridicuturing.common.scheduledweb.bean.Instance;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 实例管理器
 *
 * @author chenzhihai
 * @date 2021/02/24
 */
public class NacosInstanceManagerImpl extends AbstractInstanceManagerImpl {

    @Value("${spring.application.name:}")
    private String applicationName;

    @Value("${spring.cloud.nacos.discovery.server-addr:}")
    private String nacosAddress;

    /**
     * 是否使用nacos做注册中心
     */
    @Getter
    private boolean useNacos = false;

    private LinkedHashSet<Instance> initInstances;


    /**
     * 缓存并返回当前的所有实例
     *
     * @return {@link List<Instance>} 不会返回null，只会返回集合
     */
    @Override
    public synchronized Instance[] loadAndGetInstance() {
        LinkedHashSet<Instance> instances = new LinkedHashSet<>(initInstances);
        if (useNacos) {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url("http://" + nacosAddress + "/nacos/v1/ns/instance/list?serviceName=" + applicationName)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                List<Instance> nacosInstances = JSONObject.parseObject(response.body().string()).getJSONArray("hosts").toJavaList(Instance.class);
                instances.addAll(nacosInstances);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        lastLoadTime = System.currentTimeMillis();
        instancesCache = instances;
        return instances.toArray(new Instance[0]);
    }


    @Override
    public void init() {
        initInstances = new LinkedHashSet<>();
        initInstances.add(new Instance(LOCAL_IP, port));
        if (checkNacos()) {
            initInstances.add(new Instance(Instance.BOARDCAST, 0));
        }
    }

    private boolean checkNacos() {
        useNacos = !"".equals(nacosAddress);
        return useNacos;
    }
}
