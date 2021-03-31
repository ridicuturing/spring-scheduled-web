package com.ridicuturing.common.scheduledweb.config;

import org.springframework.lang.Nullable;

import java.util.concurrent.Executor;

/**
 * TaskWebConfigurer
 *
 * @author chenzhihai
 * @date 2021/01/22
 */
public interface TaskExcutorConfigurer {
    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Nullable
    default Executor getAsyncExecutor() {
        return null;
    }

}
