package org.r.base.auth.config;

import lombok.Data;

/**
 * @author casper
 * @date 20-3-6 下午4:08
 **/
@Data
public class QqConfig {


    /**
     * qq给出的id
     */
    private String clientId;

    /**
     * qq给出的秘钥
     */
    private String clientSecret;

    /**
     * 授权类型，默认是authorization_code
     */
    private String grantType = "authorization_code";

    /**
     * 授权成功的重定向的地址，设置为qq备案的地址
     */
    private String redirectUri;

    /**
     * qq给出的授权网关
     */
    private String authUrl;

}
