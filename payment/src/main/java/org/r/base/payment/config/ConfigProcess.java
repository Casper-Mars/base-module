package org.r.base.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author casper
 * @date 19-12-24 下午2:59
 **/
//@Service
public class ConfigProcess implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${payment.way}")
    private String payments;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        String[] split = payments.split(",");
        if(split.length<=0 || split[0] == null){
            return;
        }

        for (String payment : split) {

        }
    }

    /**
     * 配置三方支付bean
     * @param payment 三方支付
     * @param beanFactory
     */
    private void configPayment(String payment,AutowireCapableBeanFactory beanFactory){
        switch (payment){
            case "alipay":

                break;
            case "wechat":
                break;
            case "paypal":
                break;
        }
    }

}
