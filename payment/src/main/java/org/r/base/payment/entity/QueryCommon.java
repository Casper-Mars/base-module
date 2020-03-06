package org.r.base.payment.entity;

import lombok.Data;

/**
 * @author casper
 * @date 19-12-24 下午5:25
 **/
@Data
public class QueryCommon {

    /**
     * 外部系统的订单号
     */
    private String tradeNo;

    public QueryCommon(String tradeNo) {
        this.tradeNo = tradeNo;
    }


}
