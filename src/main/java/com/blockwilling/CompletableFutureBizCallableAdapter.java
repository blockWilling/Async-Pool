package com.blockwilling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by blockWilling on 2022/8/1.
 */
@Component
@Slf4j
public class CompletableFutureBizCallableAdapter extends AbstractBizCallableAdapter {
    @Autowired
    List<BizCallableAdapter>  bizCallableAdapters;

    @Override
    public boolean supports(Object result) {
        return result instanceof CompletableFuture;
    }

    @Override
    public void handle(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params) {
        CompletableFuture<Object> future = (CompletableFuture) result;
        future.thenAccept(resultObject -> internalHandle(asyncContext, resultObject, uri, params))
                .exceptionally(e -> {
                    internalHandle(asyncContext, "", uri, params);
                    return null;
                });
    }
    void internalHandle(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params){
        for (BizCallableAdapter bizCallableAdapter : bizCallableAdapters) {
            if(bizCallableAdapter.supports(result)){
                bizCallableAdapter.handle(asyncContext,result,uri,params);
                return;
            }
        }
    }
}
