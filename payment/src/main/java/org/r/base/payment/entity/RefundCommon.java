package org.r.base.payment.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author casper
 */
@Data
public class RefundCommon {


    /**
     * 商户订单号
     */
    private String outTraceNo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 第三方的订单号
     */
    private String traceNo;

    /**
     * 订单金额，单位为元
     */
    private BigDecimal orderFee;

    /**
     * 退款金额，单位为元
     */
    private BigDecimal refundFee;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
     */
    private String outRequestNo;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 交易币种
     */
    private String currency;


}
