package org.r.base.common.servicce;

import okhttp3.Request;
import org.r.base.common.pojo.RequestBo;

/**
 * @author casper
 * @date 19-10-16 下午2:32
 **/
public interface HttpRequestStrategy {


    /**
     * 执行请求
     *
     * @param requestBo 请求信息
     * @return
     */
    Request buildRequest(RequestBo requestBo);


}
