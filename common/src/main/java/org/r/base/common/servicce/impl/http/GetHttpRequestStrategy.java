package org.r.base.common.servicce.impl.http;

import okhttp3.Request;
import org.r.base.common.pojo.RequestBo;
import org.r.base.common.servicce.HttpRequestStrategy;
import org.springframework.stereotype.Service;

/**
 * @author casper
 * @date 19-10-16 下午2:44
 **/
public class GetHttpRequestStrategy implements HttpRequestStrategy {


    /**
     * 执行请求
     *
     * @param requestBo 请求信息
     * @return
     */
    @Override
    public Request buildRequest(RequestBo requestBo) {
        Request.Builder builder = new Request.Builder();
        builder.url(requestBo.getUrl());
        return builder.build();
    }
}
