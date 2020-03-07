package org.r.base.common.utils;

import org.r.base.common.pojo.RequestBo;
import org.r.base.common.pojo.RespondBo;
import org.r.base.common.servicce.HttpRequestService;
import org.r.base.common.servicce.impl.http.DefaultHttpServiceImpl;

/**
 * @author casper
 * @date 20-3-6 下午11:01
 **/
public class HttpUtils {

    private static HttpRequestService httpRequestService = new DefaultHttpServiceImpl();


    private HttpUtils() {
    }


    public void setService(HttpRequestService service){
        this.httpRequestService = service;
    }


    public static RespondBo doRequest(RequestBo requestBo){
        return httpRequestService.doRequest(requestBo);
    }






}
