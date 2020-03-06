package org.r.base.payment.service.impl.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import lombok.extern.slf4j.Slf4j;
import org.r.base.payment.entity.PayCommon;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author casper
 */
@Service("alipayMobilePaymentPlugin")
@Slf4j
public class AlipayMobilePaymentServiceImpl extends AbstractAlipayServiceImpl<AlipayTradeAppPayRequest, AlipayTradeAppPayResponse> {

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
    protected AlipayTradeAppPayRequest buildPayParam(PayCommon requestParam) {

        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        AlipayTradeAppPayRequest payRequest = new AlipayTradeAppPayRequest();
        /*自定义数据，不知道设置什么的*/
        model.setBody(requestParam.getBody());
        /*自定义数据，不知道设置什么的*/
        model.setSubject(requestParam.getTitle());
        //请保证OutTradeNo值每次保证唯一，唯一识别号，订单id
        model.setOutTradeNo(requestParam.getOutTradeNo().getOutTradeNo());
        /*设置超时时间*/
        model.setTimeoutExpress("30m");
        /*设置金额*/
        model.setTotalAmount(requestParam.getPayAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        /*不知道什么参数，不用管*/
        model.setProductCode("QUICK_MSECURITY_PAY");
        payRequest.setBizModel(model);
        /*设置回调地址*/
        payRequest.setNotifyUrl(requestParam.getNotifyUrl());
        return payRequest;
    }


    /**
     * 支付接口
     *
     * @param payCommon 支付参数，不同实现类参数不同
     * @return
     */
    @Override
    public String pay(PayCommon payCommon) {

        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        AlipayTradeAppPayRequest payRequest = buildPayParam(payCommon);
        String result = null;
        try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(payRequest);
            result = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
    }


}
