package org.r.base.payment.service.impl.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author casper
 * @date 19-12-23 上午10:41
 **/
@Service("paypalPcPaymentPlugin")
public class PaypalPcPaymentServiceImpl extends AbstractPaypalPaymentServiceImpl {


    /**
     * 构建返回值
     *
     * @param payment
     * @return
     */
    @Override
    protected String buildResult(Payment payment) {

        Optional<Links> first = payment.getLinks().stream().filter(t -> "approval_url".equals(t.getRel())).findFirst();
        return first.map(Links::getHref).orElse(null);
    }
}
