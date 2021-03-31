package com.ridicuturing.common.scheduledweb.component;


import com.ridicuturing.common.scheduledweb.bean.TaskVO;
import com.ridicuturing.common.scheduledweb.manager.InstanceManager;
import com.ridicuturing.common.scheduledweb.controller.TaskContoller;
import com.ridicuturing.common.scheduledweb.manager.TaskManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.*;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * task任务组件
 *
 * @author chenzhihai
 * @date 2021/01/13
 */
public class TasksWebComponent {

    @Value("${scheduledweb.web-prefix:}")
    private String webPrefix;

    @Resource
    private InstanceManager instanceManager;

    @Resource
    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    @Resource
    private TaskManager taskManager;

    public static String getKey(ScheduledTask scheduledTask) {
        return ((ScheduledMethodRunnable) scheduledTask.getTask().getRunnable()).getMethod().toString().replaceAll(" ", "");
    }

    public String getExpression(Task task) {
        if (task instanceof TriggerTask) {
            return ((CronTask) task).getExpression();
        }
        if (task instanceof FixedDelayTask) {
            return "FixedDelay:" + ((FixedDelayTask) task).getInterval();
        }
        if (task instanceof FixedRateTask) {
            return "FixedRate:" + ((FixedRateTask) task).getInterval();
        }
        return "fail to get the expression from unknown task type";
    }

    /**
     * 项目初始化
     */
    public void init() {
        if (null != scheduledAnnotationBeanPostProcessor) {
            scheduledAnnotationBeanPostProcessor.getScheduledTasks().forEach(a -> {
                taskManager.getAllTaskVOMap().put(getKey(a), createTaskVO(a));
                if (isCustomize(a)) {
                    taskManager.getTaskVOMap().put(getKey(a), createTaskVO(a));
                }
            });
        }
        instanceManager.init();
    }

    public boolean isCustomize(ScheduledTask t) {
        Class<?> c = ((ScheduledMethodRunnable) t.getTask().getRunnable()).getTarget().getClass();
        String path = c.getCanonicalName();
        int index = path.indexOf('$');
        path = index == -1 ? path : path.substring(0, index);
        java.net.URL url = this.getClass().getResource("/" + path.replace('.', '/') + ".class");
        return null != url && url.getPath().contains("/classes");
    }

    private TaskVO createTaskVO(ScheduledTask scheduledTask) {
        TaskVO taskVO = new TaskVO();
        taskVO.setTaskSignature(scheduledTask.getTask().toString());
        taskVO.setExpression(getExpression(scheduledTask.getTask()));
        taskVO.setCount(new AtomicInteger(0));
        taskVO.setScheduledTask(scheduledTask);
        taskVO.setUrl(webPrefix + TaskContoller.RUN_URL + "?taskSignature=" + getKey(scheduledTask));
        return taskVO;
    }


}
