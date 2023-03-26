package com.blockwilling;

import javax.servlet.AsyncContext;
import java.util.Map;

/**
 * Created by blockWilling on 2022/8/1.
 */
public interface BizCallableAdapter {
    boolean supports(Object ret);

    void handle(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params);
}
