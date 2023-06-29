package com.example.project2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncServer {


    /*    @Async("async")注解是Spring框架中的一个注解，用于标识一个方法是异步方法。通过在方法上添加@Async注解，并指定一个线程池的名称，可以告诉Spring框架将该方法作为一个异步方法进行处理。
         @Async("async")注解的作用包括：
        异步执行：通过添加@Async注解，可以将一个方法标识为异步方法，告诉Spring框架该方法需要在单独的线程中执行。这样可以避免阻塞主线程，提高应用程序的性能和响应速度。
        线程池管理：通过指定一个线程池的名称，可以告诉Spring框架使用指定的线程池来执行异步方法。这样可以灵活地管理线程池，控制线程的数量和资源的使用。
        异常处理：在异步方法中，如果发生异常，Spring框架会将异常封装为一个Future对象，并将其返回给调用方。调用方可以通过Future对象来获取异步方法的执行结果或者处理异常。
        总之，@Async("async")注解的作用是将一个方法标识为异步方法，并指定一个线程池的名称来执行异步方法。在Spring Boot应用程序中，通常会使用@Async注解来实现异步方法的执行。*/
    @Async("async")
    public void asyncTest(Integer counter) throws Exception {
        Thread.sleep(2000);
        log.info("线程：" + Thread.currentThread().getName() + "执行异步任务：" + counter);
    }
}
