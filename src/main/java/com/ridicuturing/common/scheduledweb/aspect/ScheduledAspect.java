package com.ridicuturing.common.scheduledweb.aspect;

import com.ridicuturing.common.scheduledweb.bean.TaskVO;
import com.ridicuturing.common.scheduledweb.manager.TaskManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Scheduled切面
 * 记录任务调动信息
 *
 * @author chenzhihai
 * @date 2021/01/13
 */
@Aspect
public class ScheduledAspect {

    @Resource
    private TaskManager taskManager;

    @Around("@annotation(scheduled)")
    public void recordReplaced(ProceedingJoinPoint joinPoint, Scheduled scheduled) throws Throwable {
        long startTime = System.currentTimeMillis();
        TaskVO taskVO = taskManager.getTaskVOMap().get(joinPoint.getSignature().toLongString().replaceAll(" ", ""));
        if (null != taskVO) {
            taskVO.setLastExecutionTime(new Date(startTime));
            taskVO.setCostTime(null);
            taskVO.getCount().incrementAndGet();
        }

        joinPoint.proceed();

        if (null != taskVO) {
            taskVO.setCostTime(System.currentTimeMillis() - startTime);
        }
    }

}
