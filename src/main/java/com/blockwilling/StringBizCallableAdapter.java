package com.blockwilling;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by blockWilling on 2022/8/1.
 */
@Component
@Slf4j
public class StringBizCallableAdapter extends AbstractBizCallableAdapter {
    @Override
    public boolean supports(Object result) {
        return result instanceof String;
    }

    @Override
    public void handle(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params) {
        HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
        try {
            write(resp, (String)result);
        } catch (Throwable e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //程序内部错误
            log.error("write response error, uri : {},  params : {}", uri, JSON.toJSONString(params), e);
        } finally {
            asyncContext.complete();
        }
    }
}
