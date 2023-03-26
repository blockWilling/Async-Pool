package com.blockwilling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 默认的业务异步上下文实现，使用业务线程池做隔离，并添加自定义的线程池策略
 * Created by blockWilling on 2022/7/29.
 */
@Component
@Slf4j
public class CustomizedAsyncContext implements BizAsyncContext,InitializingBean,DisposableBean {
    @Autowired
    List<BizCallableAdapter> bizCallableAdapters;
    //以下取自tomcat9默认配置
    private static final Integer asyncTimeoutInSeconds = 30;
    private static final Integer queueCapacity = Integer.MAX_VALUE;
    private static final Integer maxThreadCount = 200;
    private static final Integer minThreadCount = 10;
    private static final Integer maxIdleInSeconds = 60;
    @Autowired(required = false)
    AsyncListener listener;

    @Autowired(required = true)
    RejectedExecutionHandler executionHandler;

    ThreadPoolExecutor executor;

    BlockingDeque queue;

    @Override
    public void submit(HttpServletRequest req, Callable<Object> task) {
        final String uri = req.getRequestURI();
        final Map<String, String[]> params = req.getParameterMap();
        final AsyncContext asyncContext = req.startAsync();
        asyncContext.getRequest().setAttribute("uri", uri);
        asyncContext.getRequest().setAttribute("params", params);
        asyncContext.setTimeout(asyncTimeoutInSeconds * 1000);
        if (listener != null) {
            asyncContext.addListener(listener);
        }
        executor.submit(new WrappedCallable(asyncContext) {
            @Override
            public Object call() throws Exception{
                Object result = task.call();
                for (BizCallableAdapter bizCallableAdapter : bizCallableAdapters) {
                    if(bizCallableAdapter.supports(result)){
                        bizCallableAdapter.handle(asyncContext,result,uri,params);
                        return null;
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        queue = new LinkedBlockingDeque<Runnable>(queueCapacity);
        executor = new ThreadPoolExecutor(
                minThreadCount, maxThreadCount,
                maxIdleInSeconds* 1000, TimeUnit.SECONDS,
                queue);
        //如果核心线程超过keep alive时间也会被终止
        executor.allowCoreThreadTimeOut(true);
        executor.setRejectedExecutionHandler(executionHandler);
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }
}
