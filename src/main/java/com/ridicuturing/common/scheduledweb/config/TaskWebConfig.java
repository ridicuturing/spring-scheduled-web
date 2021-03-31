package com.ridicuturing.common.scheduledweb.config;

import com.ridicuturing.common.scheduledweb.aspect.ScheduledAspect;
import com.ridicuturing.common.scheduledweb.component.TasksWebComponent;
import com.ridicuturing.common.scheduledweb.manager.InstanceManager;
import com.ridicuturing.common.scheduledweb.manager.TaskManager;
import com.ridicuturing.common.scheduledweb.manager.impl.NacosInstanceManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * 获取scheduled任务配置
 *
 * @author chenzhihai
 * @date 2021/01/13
 */
@Configuration
@ComponentScan("com.ridicuturing.common.scheduledweb")
public class TaskWebConfig {

    @EventListener(ContextRefreshedEvent.class)
    public void init(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        TasksWebComponent tasksWebComponent = applicationContext.getBean(TasksWebComponent.class);
        tasksWebComponent.init();
    }

    @Bean
    @ConditionalOnMissingBean(TasksWebComponent.class)
    public TasksWebComponent tasksWebComponent() {
        return new TasksWebComponent();
    }

    @Bean
    @ConditionalOnMissingBean(InstanceManager.class)
    public InstanceManager instanceManager() {
        return new NacosInstanceManagerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(TaskManager.class)
    public TaskManager taskManager() {
        return new TaskManager();
    }

    @Bean
    @ConditionalOnMissingBean(ScheduledAspect.class)
    public ScheduledAspect scheduledAspect() {
        return new ScheduledAspect();
    }
}
