package com.blockwilling;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

/**
 * 业务异步上下文
 * Created by blockWilling on 2022/7/29.
 */
public interface BizAsyncContext {

    void submit(final HttpServletRequest req, final Callable<Object> task);
}
