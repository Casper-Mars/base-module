package org.r.base.common.pojo;

import lombok.Data;

/**
 * @author casper
 * @date 19-10-16 下午2:36
 **/
@Data
public class RespondBo {


    /**
     * 请求结果
     */
    private Object result;

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 响应码
     */
    private int code;

}
