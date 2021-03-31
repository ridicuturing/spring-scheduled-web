package com.ridicuturing.common.scheduledweb.controller;


import com.ridicuturing.common.scheduledweb.bean.Instance;
import com.ridicuturing.common.scheduledweb.manager.InstanceManager;
import com.ridicuturing.common.scheduledweb.bean.TaskResult;
import com.ridicuturing.common.scheduledweb.manager.TaskManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;


/**
 * 任务controller
 *
 * @author chenzhihai
 * @date 2021/01/22
 */
@Controller
public class TaskContoller {

    @Resource
    private TaskManager taskManager;
    @Resource
    private InstanceManager instanceManager;

    public static final String RUN_URL = "/tasks/run";

    /**
     * 列出项目中的定时任务
     *
     * @return {@link ArrayList}
     */
    @ResponseBody
    @GetMapping("${scheduledweb.web-prefix:}/tasks/list")
    public ArrayList list() {
        return new ArrayList(taskManager.getTaskVOMap().values());
    }

    /**
     * 列出包括maven导入包的定时任务
     *
     * @return {@link ArrayList}
     */
    @ResponseBody
    @GetMapping("${scheduledweb.web-prefix:}/tasks/allList")
    public ArrayList allList() {
        return new ArrayList(taskManager.getAllTaskVOMap().values());
    }

    @ResponseBody
    @RequestMapping("${scheduledweb.web-prefix:}" + RUN_URL)
    public TaskResult run(String taskSignature, String IpAndPort, Boolean async) {
        return taskManager.runTask(taskSignature, IpAndPort, async);
    }

    @GetMapping("${scheduledweb.web-prefix:}/tasks")
    public String index() {
        return "tasks-web.html";
    }

    @ResponseBody
    @GetMapping("${scheduledweb.web-prefix:}/instances")
    public Instance[] instances() {
        return instanceManager.getInstances();
    }


}
