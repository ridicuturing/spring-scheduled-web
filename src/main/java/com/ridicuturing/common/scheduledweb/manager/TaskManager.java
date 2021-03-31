package com.ridicuturing.common.scheduledweb.manager;

import com.alibaba.fastjson.JSONObject;
import com.ridicuturing.common.scheduledweb.bean.Instance;
import com.ridicuturing.common.scheduledweb.bean.TaskResult;
import com.ridicuturing.common.scheduledweb.bean.TaskVO;
import com.ridicuturing.common.scheduledweb.config.TaskExcutorConfigurer;
import com.ridicuturing.common.scheduledweb.controller.TaskContoller;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * task管理器
 *
 * @author chenzhihai
 * @date 2021/02/24
 */
@Getter
@Log4j2
public class TaskManager {

    /**
     * 所有task
     */
    private final Map<String, TaskVO> allTaskVOMap = new HashMap<>(16);

    /**
     * 项目本身的task（不包含第三方框架的task）
     */
    private final Map<String, TaskVO> taskVOMap = new HashMap<>(16);


    /**
     * 是否异步
     */
    @Value("${scheduledweb.isAsync:false}")
    private boolean asyncStatus;

    @Value("${scheduledweb.web-prefix:}")
    private String webPrefix;

    @Resource
    private InstanceManager instanceManager;

    OkHttpClient okHttpClient = new OkHttpClient();

    private static Executor executor = new ThreadPoolExecutor(
            10
            , 20
            , 60
            , TimeUnit.SECONDS
            , new LinkedBlockingQueue<>(1024)
            , new CustomizableThreadFactory("myTask-pool-"), new ThreadPoolExecutor.CallerRunsPolicy()
    );

    static {
        ((ThreadPoolExecutor) executor).allowCoreThreadTimeOut(true);
    }

    /**
     * 通过注入收集所有 {@link TaskExcutorConfigurer} bean
     */
    @Autowired(required = false)
    void setConfigurers(Collection<TaskExcutorConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException("不允许定义多个TaskWebConfigurer！");
        }
        TaskExcutorConfigurer configurer = configurers.iterator().next();
        executor = configurer.getAsyncExecutor();
    }

    /**
     * 广播执行
     * 对nacos注册中心中本服务的所有实例进行任务调度
     *
     * @param taskSignature 任务的签名
     * @return 执行结果信息
     */
    public TaskResult broadcast(String taskSignature) {
        int successCount = 0;
        Instance[] instances = instanceManager.getInstances();
        for (Instance instance : instances) {
            if(instance != Instance.BROADCAST_INSTANCE){
                TaskResult taskResult = runTask(taskSignature, instance);
                if (null != taskResult && taskResult.getCode() == TaskResult.SUCCESS_CODE) {
                    successCount++;
                }
            }
        }
        return TaskResult.buildSuccessResult("应执行机器数量：" + instances.length + "实际执行数量：" + successCount);
    }

    public TaskResult runTask(String taskSignature, String IpAndPort, Boolean async) {
        if (StringUtils.isEmpty(IpAndPort)) {
            return runTask(taskSignature, async);
        }
        Instance instance = parseIpAndPort(IpAndPort);
        if (instance == null) {
            return TaskResult.buildFailResult("ip端口参数异常!");
        }
        return runTask(taskSignature, instance);
    }

    private Instance parseIpAndPort(String ipAndPort) {
        try {
            if (ipAndPort.startsWith(Instance.BOARDCAST)) {
                return Instance.BROADCAST_INSTANCE;
            }
            int index = ipAndPort.indexOf(':');
            String ip = ipAndPort.substring(0, index);
            Integer port = Integer.parseInt(ipAndPort.substring(index + 1));
            return new Instance(ip, port);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public TaskResult runTask(String taskSignature) {
        return runTask(taskSignature, asyncStatus);
    }

    public TaskResult runTask(String taskSignature, Boolean async) {
        TaskVO taskVO = allTaskVOMap.get(taskSignature);
        if (null != taskVO) {
            if (asyncStatus || Boolean.TRUE.equals(async)) {
                executor.execute(() -> taskVO.getScheduledTask().getTask().getRunnable().run());
            } else {
                taskVO.getScheduledTask().getTask().getRunnable().run();
            }
            return TaskResult.buildSuccessResult();
        }
        return TaskResult.buildFailResult("taskSignature 找不到!");
    }

    public TaskResult runTask(String taskSignature, Instance instance) {
        if (instanceManager.isPresentInstance(instance)) {
            return runTask(taskSignature);
        } else if (Instance.BROADCAST_INSTANCE.equals(instance)) {
            return broadcast(taskSignature);
        } else {
            return runRemoteTask(taskSignature, instance);
        }
    }

    public TaskResult runRemoteTask(String taskSignature, Instance instance) {
        String url = "http://" + instance.getIp() + ":" + instance.getPort() + webPrefix + TaskContoller.RUN_URL + "?async=true&taskSignature=" + taskSignature;
        final Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return JSONObject.parseObject(response.body().string()).getObject("body", TaskResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return TaskResult.buildFailResult(e.getMessage());
        }
    }
}
