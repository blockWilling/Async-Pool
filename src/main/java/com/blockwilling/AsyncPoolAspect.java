package com.blockwilling;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 异步请求切面
 * Created by blockWilling on 2022/7/19.
 */
@Aspect
@Slf4j
@Component
public class AsyncPoolAspect {
    @Autowired
    BizAsyncContext asyncContext;


    @Around(value = "execution(* cn.fj.controller.async.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            asyncContext.submit(request, () -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable throwable) {
                    log.error("【AsyncContext】请求处理异常", requestAttributes);
                }
                return null;
            });
        } else {
            log.error("【AsyncPoolAspect】请求异常", requestAttributes);
        }
        return null;
    }
}
