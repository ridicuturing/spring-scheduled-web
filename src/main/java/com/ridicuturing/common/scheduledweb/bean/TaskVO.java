package com.ridicuturing.common.scheduledweb.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.scheduling.config.ScheduledTask;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * task任务VO
 *
 * @author chenzhihai
 * @date 2021/01/12
 */
@Data
@JsonIgnoreProperties(value = {"scheduledTask"})
public class TaskVO {
    private String taskSignature;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastExecutionTime;
    private Long costTime;
    private AtomicInteger count;
    private String expression;
    private String url;
    private ScheduledTask scheduledTask;
}
