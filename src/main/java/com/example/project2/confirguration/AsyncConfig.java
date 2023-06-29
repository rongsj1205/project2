package com.example.project2.confirguration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("async")
    public Executor doSomethingExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
       /* 核心线程数（corePoolSize）：表示线程池中保持的最小线程数。即使线程池中没有任务需要执行，核心线程也会一直存在，不会被销毁。
        最大线程数（maximumPoolSize）：表示线程池中允许的最大线程数。当线程池中的线程数达到最大线程数时，新的任务会被放入等待队列中等待执行。
        空闲线程存活时间（keepAliveTime）：表示当线程池中的线程数大于核心线程数时，空闲线程的存活时间。当线程池中的线程数大于核心线程数，并且空闲时间超过keepAliveTime时，空闲线程会被销毁。
        阻塞队列（workQueue）：表示用于存放等待执行的任务的队列。当线程池中的线程数达到核心线程数时，新的任务会被放入阻塞队列中等待执行。
        这些参数的设置可以根据具体的业务需求和系统资源情况进行调整。合理的设置这些参数可以提高线程池的性能和效率，避免资源浪费和任务堆积。*/
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setQueueCapacity(10);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setThreadNamePrefix("asyncxc-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolTaskExecutor;
    }

}
