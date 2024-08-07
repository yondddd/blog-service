package com.yond.blog.schedule;

import com.yond.blog.entity.ScheduleJobDO;
import com.yond.blog.entity.ScheduleJobLogDO;
import com.yond.blog.service.ScheduleJobService;
import com.yond.common.utils.json.util.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description: 单位同步配置定时任务
 * @Author: WangJieLong
 * @Date: 2023-10-30
 */
@Configuration
@EnableScheduling
public class BlogSchedulingConfigurer implements SchedulingConfigurer {

    public BlogSchedulingConfigurer(@Lazy ScheduleJobService scheduleJobService, TaskScheduler taskScheduler) {
        this.scheduleJobService = scheduleJobService;
        this.taskScheduler = taskScheduler;
    }

    private final TaskScheduler taskScheduler;

    private final ScheduleJobService scheduleJobService;

    private ScheduledTaskRegistrar taskRegistrar;

    private final Map<String, ScheduledTask> scheduledTasks = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogSchedulingConfigurer.class);

    @Override
    @Async
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler);
        this.taskRegistrar = taskRegistrar;
        List<ScheduleJobDO> jobList = scheduleJobService.getJobList();
        for (ScheduleJobDO job : jobList) {
            addJob(job);
        }
    }

    public void addJob(ScheduleJobDO job) {
        ScheduledTaskWrapper task = null;
        try {
            task = new ScheduledTaskWrapper(new ScheduleRunnable(job.getJobId(), job.getBeanName(), job.getMethodName(), job.getParams()));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask(new CronTask(task, job.getCron()));
        scheduledTasks.put(String.valueOf(job.getJobId()), scheduledTask);
        LOGGER.info("<|>BlogSchedulingConfigurer_addJob<|>job:{}<|>", JsonUtils.toJson(job));
    }

    public void removeJob(String jobId) {
        ScheduledTask scheduledTask = scheduledTasks.remove(jobId);
        if (scheduledTask != null) {
            scheduledTask.cancel();
            LOGGER.info("<|>BlogSchedulingConfigurer_removeJob<|>jobId:{}<|>", JsonUtils.toJson(jobId));
        } else {
            LOGGER.error("<|>BlogSchedulingConfigurer_removeJob_notFound<|>jobId:{}<|>", JsonUtils.toJson(jobId));
        }
    }

    public void updateJob(ScheduleJobDO job) {
        this.removeJob(String.valueOf(job.getJobId()));
        this.addJob(job);
        LOGGER.info("<|>BlogSchedulingConfigurer_updateJob<|>job:{}<|>", JsonUtils.toJson(job));
    }

    public void runJob(String jobId) {
        ScheduledTask task = scheduledTasks.get(jobId);
        if (task != null) {
            task.getTask().getRunnable().run();
            LOGGER.info("<|>BlogSchedulingConfigurer_runJob<|>jobId:{}<|>", JsonUtils.toJson(jobId));
        }
    }


    private class ScheduledTaskWrapper implements Runnable {

        private final ScheduleRunnable task;

        ScheduledTaskWrapper(ScheduleRunnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            ScheduleJobDO job = scheduleJobService.getJobById(task.getJobId());
            long start = System.currentTimeMillis();
            ScheduleJobLogDO jobLog = new ScheduleJobLogDO();
            jobLog.setJobId(job.getJobId());
            jobLog.setBeanName(job.getBeanName());
            jobLog.setMethodName(job.getMethodName());
            jobLog.setParams(job.getParams());
            jobLog.setCreateTime(new Date(start));
            try {
                task.run();
                jobLog.setStatus(true);
            } catch (Exception e) {
                jobLog.setStatus(false);
                jobLog.setError(e.toString());
            } finally {
                long duration = System.currentTimeMillis() - start;
                jobLog.setTimes((int) duration);
                scheduleJobService.saveJobLog(jobLog);
            }
        }
    }

}
