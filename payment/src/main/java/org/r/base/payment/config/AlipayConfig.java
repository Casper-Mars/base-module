package org.r.base.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;

/**
 * @author casper
 */
@Data
public class AlipayConfig implements PaymentConfig{

    /**
     * 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
     */
    private String returnUrl;
    /**
     * 设置未付款交易的超时时间 默认30分钟，一旦超时，该笔交易就会自动被关闭。 取值范围：1m～15d。
     * m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，如1.5h，可转换为90m。
     */
    private String outOfTime;

    /**
     * 合作身份者ID，以2088开头由16位纯数字组成的字符串
     */
    private String partner;

    /**
     * 商户的私钥
     */
    private String privateKey;

    /**
     * 支付宝的公钥
     */
    private String aliPublicKey;

    /**
     * 字符编码格式 目前支持 gbk 或 utf-8
     */
    private String inputCharset;

    /**
     * 签名方式 不需修改
     */
    private String signType;

    /**
     * 卖家支付宝账号
     */
    private String sellerId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 数据格式
     */
    private String format;

    /**
     * 官网的支付宝网关
     */
    private String gatewayUrl;


    /**
     * 获取阿里支付的客户端
     *
     * @return
     */
    public AlipayClient getAlipayClient() {

        return new DefaultAlipayClient(
                gatewayUrl,
                appId,
                privateKey,
                format,
                inputCharset,
                aliPublicKey,
                signType);
    }


}