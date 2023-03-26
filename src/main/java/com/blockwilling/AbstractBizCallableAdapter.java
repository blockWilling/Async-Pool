package com.blockwilling;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by blockWilling on 2022/8/1.
 */
public abstract class AbstractBizCallableAdapter implements BizCallableAdapter {
    public void write(HttpServletResponse resp, String s) throws IOException {
        resp.getWriter().write(s);
        resp.flushBuffer();
    }
}
