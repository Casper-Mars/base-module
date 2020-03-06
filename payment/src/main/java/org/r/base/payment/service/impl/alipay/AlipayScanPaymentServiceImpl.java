package org.r.base.payment.service.impl.alipay;

import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import org.r.base.payment.entity.PayCommon;
import org.springframework.stereotype.Service;

/**
 * @author casper
 * @date 19-10-18 下午12:44
 **/
@Service("alipayScanPaymentPlugin")
public class AlipayScanPaymentServiceImpl extends AbstractAlipayServiceImpl<AlipayTradePrecreateRequest, AlipayTradePrecreateResponse> {

    /**
     * 构造请求参数
     *
     * @param {String}     outTradeNo 账单号
     * @param {String}     body 显示的内容
     * @param {String}     title 显示的标题
     * @param {BigDecimal} payAmount 支付的金额
     * @param {String}     notifyUrl 回调接口地址
     */
    @Override
    protected AlipayTradePrecreateRequest buildPayParam(PayCommon requestParam) {
        AlipayTradePrecreateRequest payRequest = new AlipayTradePrecreateRequest();
        payRequest.setBizContent("{" +
                "    \"out_trade_no\":\"" + requestParam.getOutTradeNo().getOutTradeNo() + "\"," +
                "    \"total_amount\":\"" + requestParam.getPayAmount() + "\"," +
                "    \"subject\":\"" + requestParam.getTitle() + "\"" +
                "    }");
        payRequest.setNotifyUrl(requestParam.getNotifyUrl());
        return payRequest;
    }


}
