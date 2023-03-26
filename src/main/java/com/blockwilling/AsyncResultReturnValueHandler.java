package com.blockwilling;

import com.blockwilling.controller.async.Demo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 为了避免spring的默认匹配到的出参解析器再次写响应，而报错
 * Created by blockWilling on 2022/7/29.
 */
@Component
public class AsyncResultReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> type = returnType.getParameterType();
        return Demo.AsyncResult.class.isAssignableFrom(type);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        //do nothing
    }
}
