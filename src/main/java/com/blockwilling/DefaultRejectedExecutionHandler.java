package com.blockwilling;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by blockWilling on 2022/7/29.
 */
@Slf4j
@Component
public class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if(r instanceof WrappedCallable) {
            WrappedCallable cc = ((WrappedCallable) r);
            AsyncContext asyncContext = cc.asyncContext;
            if(asyncContext != null) {
                String uri = (String) asyncContext.getRequest().getAttribute("uri");
                Map params = (Map) asyncContext.getRequest().getAttribute("params");
                log.error("async request rejected, uri : {}, params : {}", uri, JSON.toJSONString(params));
                try {
                    HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    asyncContext.complete();
                }
            }
        }
    }
}
