package org.r.base.payment.enums;

/**
 * @author casper
 * @date 19-10-18 下午1:32
 **/
public enum PaymentEnum {


    /**
     * 阿里app支付
     */
    alipayMobile("alipayMobilePaymentPlugin", "amp"),
    /**
     * 阿里扫码支付
     */
    alipayScan("alipayScanPaymentPlugin", "asp"),
    /**
     * 阿里pc网页支付
     */
    alipayPcPage("alipayPcPagePaymentPlugin", "appp"),
    /**
     * 微信app支付
     */
    wechatMobile("wechatMobilePaymentPlugin", "wmp"),
    /**
     * 微信扫码支付
     */
    wechatScan("wechatScanPaymentPlugin", "wsp"),
    /**
     * paypal支付
     */
    paypalPc("paypalPcPaymentPlugin", "pppp"),
    /**
     * paypal移动支付
     */
    paypalMobile("paypalMobilePaymentPlugin", "pmpp"),
    ;


    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 代号
     */
    private String code;

    PaymentEnum(String serviceName, String code) {
        this.serviceName = serviceName;
        this.code = code;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCode() {
        return code;
    }


    public static PaymentEnum ofCode(String code) {
        for (PaymentEnum value : PaymentEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static PaymentEnum ofServiceName(String serviceName) {
        for (PaymentEnum value : PaymentEnum.values()) {
            if (value.serviceName.equals(serviceName)) {
                return value;
            }
        }
        return null;
    }

}
