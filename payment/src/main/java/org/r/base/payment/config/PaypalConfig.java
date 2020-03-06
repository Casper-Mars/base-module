package org.r.base.payment.config;

import com.paypal.base.rest.APIContext;
import lombok.Data;

/**
 * paypal支付的参数配置
 *
 * @author casper
 * @date 19-12-23 上午10:06
 **/
@Data
public class PaypalConfig implements PaymentConfig{


    /**
     * appId
     */
    private String clientId;
    /**
     * 密钥
     */
    private String clientSecret;
    /**
     * 接入模式,live正式,sandbox沙箱
     */
    private String mode;
    /**
     * 货币
     */
    private String currency;

    public APIContext getApiContext(){
        return new APIContext(this.getClientId(), this.getClientSecret(), this.getMode());
    }

}
