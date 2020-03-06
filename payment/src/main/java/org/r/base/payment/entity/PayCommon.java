package org.r.base.payment.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ihui
 * 支付公用参数
 */
@Data
public class PayCommon implements Serializable {

    /**
     * 支付订单号
     */
    private OutTradeNoBo outTradeNo;

    /**
     * 服务器异步通知页面路径
     */
    private String notifyUrl;

    /**
     * 取消支付时跳转的url
     */
    private String cancelUrl;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付要显示的标题
     */
    private String title;

    /**
     * 支付要显示的内容
     */
    private String body;

    /**
     * 请求支付的请求者ip地址
     */
    private String remoteIp;

    /**
     * @param paySn      订单号
     * @param amount     金额
     * @param serverPath 服务器地址，用来设置回调的接口地址
     * @param body       显示的内容
     * @param title      显示的标题
     * @return
     */
    public PayCommon(String paySn, BigDecimal amount, String serverPath, String body, String title) {
        this.outTradeNo = new OutTradeNoBo(paySn);
        this.notifyUrl = serverPath;
        this.payAmount = amount;
        this.title = title;
        this.body = body;
    }

}
