package org.r.base.auth.config;

import lombok.Data;

/**
 * @author casper
 * @date 20-3-6 下午4:08
 **/
@Data
public class WechatConfig {


    /**
     * 微信给出的id
     */
    private String appId;

    /**
     * 微信给出的秘钥
     */
    private String secret;

    /**
     * 授权类型，默认是authorization_code
     */
    private String grantType = "authorization_code";

    /**
     * 微信给出的授权网关
     */
    private String authUrl;

}
