package org.r.base.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zj
 * @date 2019/3/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDTO {

    /**
     * 支付流水号
     */
    private String tradeNo;

    /**
     * 支付状态
     */
    private boolean success;

    /**
     * 账单唯一标识
     */
    private String outTradeNo;

    /**
     * 失败的信息
     */
    private String failMsg;

    public static NotifyDTO fail() {
        return new NotifyDTO("", false, "", "");
    }

    public static NotifyDTO fail(String msg) {
        return new NotifyDTO("", false, "", msg);
    }


}
