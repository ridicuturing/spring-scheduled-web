package com.ridicuturing.common.scheduledweb.bean;

import lombok.Data;

/**
 * 结果
 *
 * @author chenzhihai
 * @date 2021/02/24
 */
@Data
public class TaskResult {
    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;

    private int code;
    private String message;

    public TaskResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static TaskResult buildSuccessResult() {
        return new TaskResult(SUCCESS_CODE, "success");
    }

    public static TaskResult buildSuccessResult(String message) {
        return new TaskResult(SUCCESS_CODE, message);
    }

    public static TaskResult buildFailResult(String message) {
        return new TaskResult(FAIL_CODE, message);
    }
}
