package org.r.base.sms.provider;

import java.util.Map;

/**
 * @author casper
 * @date 20-3-7 下午7:17
 **/
public interface SmsServiceProvider {


    /**
     * 发送短信
     *
     * @param param 参数
     * @return
     */
    String sendSms(Map<String, Object> param);


}
