package org.r.base.payment.config;

import lombok.Data;

/**
 * @author casper
 * @date 19-10-16 下午1:42
 **/
@Data
public class WechatPayConfig implements PaymentConfig {

    /**
     * appid
     */
    private String appId;
    /**
     * 应用密钥
     */
    private String appSecret;
    /**
     * 商户号
     */
    private String mchid;
    /**
     * API密钥，在商户平台设置
     */
    private String apiKey;
    /**
     * 交易类型
     */
    private String tradeType;
    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * api证书位置，绝对路径
     */
    private String p12CertPath;

    /**
     * 请求地址
     */
    private String gatewayUrl;

    /**
     * 退款地址
     */
    private String refundUrl;

    /**
     * 查询地址
     */
    private String queryUrl;


}
