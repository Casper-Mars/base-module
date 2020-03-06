package org.r.base.payment.entity;

import org.r.base.common.utils.RandomUtils;
import org.springframework.util.StringUtils;

/**
 * @author casper
 * @date 19-10-22 上午11:13
 **/
public class OutTradeNoBo {


    private final String prefix = "OTN";

    /**
     * 业务的支付编号
     */
    private String paySn;

    /**
     * 第三方支付使用的支付编号
     */
    private String outTradeNo;


    public OutTradeNoBo(String paySn) {
        this.paySn = paySn;
        this.outTradeNo = prefix + "_" + paySn + "_" + RandomUtils.getRandomNumberString(6);
    }

    public String getPaySn() {
        return paySn;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    /**
     * 从第三方支付编号中抽去支付编号
     *
     * @param outTradeNo
     * @return
     */
    public static String extractPaySn(String outTradeNo) {
        if (StringUtils.isEmpty(outTradeNo)) {
            return "";
        }
        String[] s = outTradeNo.split("_");
        if (s[0] == null || s.length < 3) {
            return "";
        }
        return s[1];
    }


}
