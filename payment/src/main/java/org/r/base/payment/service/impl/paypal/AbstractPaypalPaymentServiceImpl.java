package org.r.base.payment.service.impl.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.r.base.payment.config.PaypalConfig;
import org.r.base.payment.dto.NotifyDTO;
import org.r.base.payment.entity.PayCommon;
import org.r.base.payment.entity.QueryBo;
import org.r.base.payment.entity.QueryCommon;
import org.r.base.payment.entity.RefundCommon;
import org.r.base.payment.exception.PayException;
import org.r.base.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author casper
 * @date 19-12-23 上午10:49
 **/
@Slf4j
public abstract class AbstractPaypalPaymentServiceImpl implements PaymentService {


    @Autowired(required = false)
    private PaypalConfig paypalConfig;

    /**
     * 构建返回值
     *
     * @return
     */
    protected abstract String buildResult(Payment payment);

    /**
     * 构建请求参数
     *
     * @param payCommon 支付信息
     * @return
     */
    protected Payment buildRequest(PayCommon payCommon) {
        /*明细*/
        Details details = new Details();
        details.setSubtotal(payCommon.getPayAmount().toString());
        /*总金额*/
        Amount amount = new Amount();
        amount.setCurrency(paypalConfig.getCurrency());
        amount.setTotal(payCommon.getPayAmount().toString());
        amount.setDetails(details);
        /*交易*/
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setInvoiceNumber(payCommon.getOutTradeNo().getOutTradeNo());
        transaction.setDescription(payCommon.getBody());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        /*支付方式*/
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setNoteToPayer(payCommon.getTitle());
        // 回调地址
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(payCommon.getNotifyUrl());
        redirectUrls.setCancelUrl(payCommon.getCancelUrl());
        payment.setRedirectUrls(redirectUrls);

        return payment;
    }

    /**
     * 支付接口
     *
     * @param payCommon 支付参数，不同实现类参数不同
     * @return
     */
    @Override
    public String pay(PayCommon payCommon) throws PayException {

        String result;
        APIContext apiContext = paypalConfig.getApiContext();
        Payment payment = buildRequest(payCommon);
        try {
            result = buildResult(payment.create(apiContext));
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            throw new PayException(e.getMessage());
        }
        if (StringUtils.isEmpty(result)) {
            throw new PayException("支付出错");
        }
        return result;
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
        String paymentId = request.getParameter("paymentId");
        String payerId = request.getParameter("PayerID");
        log.info("订单{}支付回调, paymentId:{} payerId:{}", billSn, paymentId, payerId);
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        Payment execute = null;
        try {
            execute = payment.execute(paypalConfig.getApiContext(), paymentExecute);
            log.debug("订单{}支付回调结果{}", billSn, execute.toJSON());
            //校验结果
            if ("approved".equals(execute.getState())) {
                Amount amount = execute.getTransactions().get(0).getAmount();
                String currency = amount.getCurrency();
                if (!paypalConfig.getCurrency().equals(currency)) {
                    return NotifyDTO.fail("货币类型不正确");
                }
                return new NotifyDTO(paymentId, true, billSn, "");
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return NotifyDTO.fail();
    }

    /**
     * 退款接口
     *
     * @param refundCommon 退款的参数
     * @return
     */
    @Override
    public Boolean refund(RefundCommon refundCommon) throws PayException {
        try {
            /*获取卖家号*/
            Payment payment = null;
            payment = Payment.get(paypalConfig.getApiContext(), refundCommon.getTraceNo());
            Transaction transaction = payment.getTransactions().get(0);
            RelatedResources resources = transaction.getRelatedResources().get(0);
            String saleId = resources.getSale().getId();
            /*退款金额信息*/
            Amount amount = new Amount();
            if (StringUtils.isEmpty(refundCommon.getCurrency())) {
                amount.setCurrency(paypalConfig.getCurrency());
            } else {
                amount.setCurrency(refundCommon.getCurrency());
            }
            amount.setTotal(refundCommon.getRefundFee().toString());
            /*退款请求*/
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setAmount(amount);
            /*卖家信息*/
            Sale sale = new Sale();
            sale.setId(saleId);
            /*请求退款*/
            DetailedRefund refund = sale.refund(paypalConfig.getApiContext(), refundRequest);
            if ("completed".equals(refund.getState())) {
                return true;
            } else {
                throw new PayException(refund.toJSON());
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            throw new PayException(e.getMessage());
        }
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
        return null;
    }

    /**
     * 查询账单
     *
     * @param queryCommon 查询参数
     * @return
     */
    @Override
    public QueryBo query(QueryCommon queryCommon) throws PayException {
        try {
            Payment payment = Payment.get(paypalConfig.getApiContext(), queryCommon.getTradeNo());
            QueryBo queryBo = new QueryBo();
            queryBo.setSuccess(true);
            queryBo.setMetaData(payment.toJSON());
            queryBo.setData(queryBo.getMetaData());
            return queryBo;
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            throw new PayException(e.getMessage());
        }
    }
}
