package service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.r.base.payment.entity.PayCommon;
import org.r.base.payment.entity.QueryBo;
import org.r.base.payment.entity.QueryCommon;
import org.r.base.payment.entity.RefundCommon;
import org.r.base.payment.exception.PayException;
import org.r.base.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/payment-context.xml")
public class PaymentServiceTest {

    @Autowired
    @Qualifier("alipayMobilePaymentPlugin")
    private PaymentService amp;

    @Autowired
    @Qualifier("alipayScanPaymentPlugin")
    private PaymentService asp;
    @Autowired
    @Qualifier("alipayPcPagePaymentPlugin")
    private PaymentService appp;

    @Autowired
    @Qualifier("wechatMobilePaymentPlugin")
    private PaymentService wmp;

    @Autowired
    @Qualifier("wechatScanPaymentPlugin")
    private PaymentService wsp;

    @Autowired
    @Qualifier("paypalPcPaymentPlugin")
    private PaymentService pppp;

    @Autowired
    @Qualifier("paypalMobilePaymentPlugin")
    private PaymentService pmpp;


    String paySn = "POD20191016782539";


    @Test
    public void pay() throws PayException {
        PayCommon payCommon = new PayCommon(
                paySn,
                new BigDecimal("0.01"),
                "http://47.244.62.252:18080/api/api/common/notify/" + paySn,
                "test pay",
                "test pay"
        );
        payCommon.setRemoteIp("127.0.0.1");
        payCommon.setCancelUrl("http://47.244.62.252:18080/api/api/common/test");
        String pay = "";
//            pay = amp.pay(payCommon);
            pay = wsp.pay(payCommon);
//            pay = wmp.pay(payCommon);
//            pay = pmpp.pay(payCommon);
//        pay = pppp.pay(payCommon);
//            pay = asp.pay(payCommon);
//            pay = appp.pay(payCommon);
        System.out.println(pay);
    }

    @Test
    public void notifyCallBack() {
    }

//    @Test
    public void refund() {

        RefundCommon refundCommon = new RefundCommon();
        refundCommon.setTraceNo("PAYID-LWO2OPA7UN241053R845161L");
        refundCommon.setRefundFee(new BigDecimal("0.01"));
        refundCommon.setOrderFee(new BigDecimal("0.88"));
        refundCommon.setCurrency("USD");
        refundCommon.setOutTraceNo(paySn);
        refundCommon.setNotifyUrl("http://39.108.88.133/payment/post/param/" + paySn);
        refundCommon.setOutRefundNo("refund" + paySn + "123");
//
        Boolean refund = false;
        try {
//            refund = wsp.refund(refundCommon);
            refund = pppp.refund(refundCommon);
        } catch (PayException e) {
            e.printStackTrace();
        }
        System.out.println(refund ? "退款成功" : "退款失败");
    }

//    @Test
    public void refundNotifyCallBack() {
    }


//    @Test
    public void query() throws PayException {

        QueryCommon queryCommon = new QueryCommon("PAYID-LWO2OPA7UN241053R845161L");

        QueryBo query;
        query = pppp.query(queryCommon);
//        query = wsp.query(queryCommon);
        System.out.println(query.getData());

    }
}