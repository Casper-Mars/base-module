package org.r.base.common.pojo;

import lombok.Data;
import org.r.base.common.enums.RequestMethodEnum;

/**
 * 请求信息
 *
 * @author casper
 * @date 19-10-16 下午2:06
 **/
@Data
public class RequestBo {


    /**
     * 请求地址
     */
    private String url;

    /**
     * 协议类型
     */
    private ProtocolProvider protocol;

    /**
     * 请求方法
     */
    private RequestMethodEnum method;

    /**
     * 参数
     */
    private Object param;

    /**
     * content-type
     */
    private String mediaType;

}
