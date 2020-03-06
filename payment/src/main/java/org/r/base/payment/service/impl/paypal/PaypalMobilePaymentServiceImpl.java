package org.r.base.payment.service.impl.paypal;

import com.alibaba.fastjson.JSONObject;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author casper
 * @date 19-12-23 下午3:18
 **/
@Service("paypalMobilePaymentPlugin")
public class PaypalMobilePaymentServiceImpl extends AbstractPaypalPaymentServiceImpl {


    /**
     * 构建返回值
     *
     * @param payment
     * @return
     */
    @Override
    protected String buildResult(Payment payment) {
        Transaction tr = payment.getTransactions().get(0);
        Map<String, String> map = new HashMap<>(5);
        map.put("currency", tr.getAmount().getCurrency());
        map.put("note_to_payer", payment.getNoteToPayer());
        map.put("payAmount", tr.getAmount().getTotal());
        map.put("paySn", tr.getInvoiceNumber());
        return JSONObject.toJSONString(map);
    }
}
