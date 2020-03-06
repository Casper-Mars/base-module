package org.r.base.payment.service.impl.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.r.base.payment.config.AlipayConfig;
import org.r.base.payment.dto.NotifyDTO;
import org.r.base.payment.entity.*;
import org.r.base.payment.exception.PayException;
import org.r.base.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author casper
 * @date 19-10-18 下午12:59
 **/
@Slf4j
public abstract class AbstractAlipayServiceImpl<T extends AlipayRequest<R>, R extends AlipayResponse> implements PaymentService {

    @Autowired(required = false)
    protected AlipayConfig alipayConfig;

    /**
     * 构造请求参数
     *
     * @param requestParam 支付参数
     * @return 支付信息
     */
    protected abstract T buildPayParam(PayCommon requestParam);


    /**
     * 实现支付的细节,每一种支付的细节不一样,这里提供一种默认的实现方式
     *
     * @param payRequest   请求
     * @param alipayClient 执行请求的客户端类
     * @return 支付请求的结果
     * @throws AlipayApiException 支付逻辑的异常
     */
    protected R pay0(T payRequest, AlipayClient alipayClient) throws AlipayApiException {
        return alipayClient.execute(payRequest);
    }


    /**
     * 支付接口
     *
     * @param payCommon 支付参数，不同实现类参数不同
     * @return
     */
    @Override
    public String pay(PayCommon payCommon) throws PayException {

        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        try {
            R execute = pay0(buildPayParam(payCommon), alipayClient);
            return execute.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new PayException("支付宝支付失败");
        }
    }

    /**
     * 给第三方支付调用的回调方法
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param billSn   账单唯一标识，此处标识id
     * @return
     */
    @Override
    public NotifyDTO notifyCallBack(HttpServletRequest request, HttpServletResponse response, String billSn) {
        /*支付成功的标志位*/
        String successStatus = "TRADE_SUCCESS";

        String tradeNo = request.getParameter("tradeNo");
        String tradeStatus = request.getParameter("trade_status");
        String outTradeNo = request.getParameter("out_trade_no");
        outTradeNo = OutTradeNoBo.extractPaySn(outTradeNo);
        log.info("接收到支付宝异步通知:out_trade_no:" + outTradeNo + " tradeNo：" + tradeNo + " trade_status_str:" + tradeStatus);
        log.info("接收到支付宝异步通知：" + request.getParameterMap().toString());
        if (StringUtils.isEmpty(tradeNo) || StringUtils.isEmpty(tradeStatus) || StringUtils.isEmpty(outTradeNo) || !outTradeNo.equals(billSn)) {
            return NotifyDTO.fail();
        }

        if (successStatus.equals(tradeStatus)) {
            return new NotifyDTO(tradeNo, true, outTradeNo, "");
        } else {
            return NotifyDTO.fail();
        }
    }

    /**
     * 退款接口
     *
     * @param refundCommon 退款的参数
     * @return
     */
    @Override
    public Boolean refund(RefundCommon refundCommon) {
        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        AlipayTradeRefundRequest refundRequest = buildRefundParam(refundCommon);
        try {
            AlipayTradeRefundResponse execute = alipayClient.execute(refundRequest);
            if (execute.isSuccess()) {
                if (execute.getCode().equals("10000")) {
                    return true;
                } else {
                    log.error(JSONObject.toJSONString(execute));
                    throw new RuntimeException(String.format("code:%s,msg:%s", execute.getSubCode(), execute.getSubMsg()));
                }
            } else {
                throw new RuntimeException("支付宝退款调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 退款的通知回调
     *
     * @param request
     * @param response
     * @param billSn   退款唯一标志
     * @return
     */
    @Override
    public NotifyDTO refundNotifyCallBack(HttpServletRequest request, HttpServletResponse response, String billSn) {
        throw new RuntimeException("支付宝不支付退款回调");
    }

    /**
     * 查询账单
     *
     * @param queryCommon 查询参数
     * @return
     */
    @Override
    public QueryBo query(QueryCommon queryCommon) throws PayException {

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        /*构造查询参数*/
        Map<String, Object> param = new HashMap<>(10);
        param.put("trade_no", queryCommon.getTradeNo());
        request.setBizContent(JSONObject.toJSONString(param));
        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        String result;
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            log.info(response.getBody());
            QueryBo queryBo = new QueryBo();
            queryBo.setMetaData(response.getBody());
            if (response.isSuccess()) {
                result = response.getBody();
                Map<String, Object> map = JSONObject.parseObject(result);
                result = JSONObject.toJSONString(map.get("alipay_trade_query_response"));
                queryBo.setSuccess(true);
                queryBo.setData(result);
            } else {
                queryBo.setSuccess(false);
            }
            return queryBo;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new PayException("支付宝账单查询异常");
        }
    }


    /**
     * 构建退款参数
     *
     * @param refundCommon 退款的参数
     * @return
     */
    private AlipayTradeRefundRequest buildRefundParam(RefundCommon refundCommon) {
        Map<String, Object> param = new HashMap<>(10);
        param.put("trade_no", refundCommon.getTraceNo());
        param.put("out_trade_no", refundCommon.getOutTraceNo());
        param.put("refund_amount", refundCommon.getRefundFee());
        param.put("refund_reason", refundCommon.getRefundReason());
        param.put("out_request_no", refundCommon.getOutRequestNo());

        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        String bizContent = JSONObject.toJSONString(param);
        log.info("退款请求参数：{}", bizContent);
        refundRequest.setBizContent(bizContent);
        return refundRequest;
    }
}
