package com.blockwilling;

import lombok.Data;

import javax.servlet.AsyncContext;
import java.util.concurrent.Callable;

/**
 * Created by blockWilling on 2022/7/29.
 */
@Data
public abstract class WrappedCallable implements Callable {
    AsyncContext asyncContext;

    public WrappedCallable(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }
}
