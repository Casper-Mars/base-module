package org.r.base.payment.service.impl.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.r.base.payment.entity.PayCommon;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author casper
 * @date 19-12-20 下午1:37
 **/
@Service("alipayPcPagePaymentPlugin")
public class AlipayPcPagePaymentServiceImpl extends AbstractAlipayServiceImpl<AlipayTradePagePayRequest, AlipayTradePagePayResponse> {

    /**
     * 构造请求参数
     *
     * @param requestParam 支付参数
     * @return 支付信息
     */
    @Override
    protected AlipayTradePagePayRequest buildPayParam(PayCommon requestParam) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        Map<String, Object> params = new HashMap<>(5);
        params.put("out_trade_no", requestParam.getOutTradeNo().getOutTradeNo());
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");
        params.put("total_amount", requestParam.getPayAmount().toString());
        params.put("subject", requestParam.getTitle());
        params.put("body", requestParam.getBody());
        request.setBizContent(JSONObject.toJSONString(params));
        return request;
    }

    /**
     * 实现支付的细节,每一种支付的细节不一样,这里提供一种默认的实现方式
     *
     * @param payRequest   请求
     * @param alipayClient 执行请求的客户端类
     * @return 支付请求的结果
     * @throws AlipayApiException 支付逻辑的异常
     */
    @Override
    protected AlipayTradePagePayResponse pay0(AlipayTradePagePayRequest payRequest, AlipayClient alipayClient) throws AlipayApiException {

        return alipayClient.pageExecute(payRequest);
    }
}
