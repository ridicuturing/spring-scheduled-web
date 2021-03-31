# scheduled-web-spring-boot-starter
本项目已无侵入式的方式，提供一个简易web页面去监控和直接调用spring的scheduled定时任务

# 使用方法
只要服务本身支持@Scheduled,直接引用该插件即可。

极其简洁的前端页面:
![](https://raw.githubusercontent.com/ridicuturing/spring-scheduled-web/master/img/picture1.png)

## 分布式配置
本项目默认支持基于nacos注册中心，可远程命令同服务下的其他实例执行任务。
实现方法是通过nacos官方API（http://" + {spring.cloud.nacos.discovery.server-addr} + "/nacos/v1/ns/instance/list?serviceName=" + applicationName）获取相关实例。

# 部分额外配置
``` bash
scheduledweb.web-prefix = /extends #若接口与项目有冲突，可通过此配置定义包接口的前缀
scheduledweb.isAsync = true #通过网页调用定时任务时，默认同步，可开启异步
```

## 异步线程池配置
开启异步后,默认使用
```
new ThreadPoolExecutor(
                    10
                    , 20
                    , 4
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<>(1024)
                    , new CustomizableThreadFactory("myAsync-pool-"), new ThreadPoolExecutor.CallerRunsPolicy()
            );
```
要定制的话可以参考如下:
```java
@Configuration
public class TaskExcutor implements TaskExcutorConfigurer {

    @Value("${thread.pool.corePoolSize:20}")
    private int corePoolSize;

    @Value("${thread.pool.maxPoolSize:30}")
    private int maxPoolSize;

    @Value("${thread.pool.keepAliveSeconds:4}")
    private int keepAliveSeconds;

    @Value("${thread.pool.queueCapacity:4096}")
    private int queueCapacity;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueCapacity),new CustomizableThreadFactory("myAsync-pool-"), new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
```
