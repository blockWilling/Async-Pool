package com.blockwilling;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by blockWilling on 2022/7/29.
 */
@Slf4j
@Component
public class DefaultAsyncListener implements AsyncListener {
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        AsyncContext asyncContext = event.getAsyncContext();
        String uri = (String) asyncContext.getRequest().getAttribute("uri");
        Map params = (Map) asyncContext.getRequest().getAttribute("params");
        log.error("async request timeout, uri : {}, params : {}", uri, JSON.toJSONString(params));
        try {
            HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            asyncContext.complete();
        }
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        AsyncContext asyncContext = event.getAsyncContext();
        Throwable throwable = event.getThrowable();
        String uri = (String) asyncContext.getRequest().getAttribute("uri");
        Map params = (Map) asyncContext.getRequest().getAttribute("params");
        log.error("async request error, uri : {}, params : {}, ex : {}", uri, JSON.toJSONString(params),throwable.toString());
        try {
            HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            asyncContext.complete();
        }
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {

    }
}
