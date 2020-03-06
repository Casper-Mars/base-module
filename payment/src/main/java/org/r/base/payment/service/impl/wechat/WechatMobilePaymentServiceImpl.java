package org.r.base.payment.service.impl.wechat;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.r.base.common.utils.XMLUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author casper
 * @date 19-10-16 下午1:39
 **/
@Service("wechatMobilePaymentPlugin")
@Slf4j
public class WechatMobilePaymentServiceImpl extends AbstractWechatServiceImpl {


    /**
     * 获取交易类型:app,native
     *
     * @return
     */
    @Override
    protected String getTradeType() {
        return "APP";
    }

    /**
     * 封装返回值
     *
     * @param xml 请求结果
     */
    @Override
    protected String buildResult(XMLUtils xml) {

        String prepayId = xml.getText(xml.getRootElement(), "prepay_id");
        /*-------------------------------------------------------封装返回值-----------------------------------------------*/
        /*构造参数map，便于之后生成签名，使用treemap为了获得排序集合，满足微信支付文档的要求*/
        Map<String, String> param = new TreeMap<>(String::compareToIgnoreCase);
        param.put("appid", wechatPayConfig.getAppId());
        param.put("partnerid", wechatPayConfig.getMchid());
        param.put("prepayid", prepayId);
        param.put("package", "Sign=WXPay");
        param.put("noncestr", this.createNoncestr());
        param.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        param.put("sign", this.sign(param, wechatPayConfig.getApiKey()));
        return JSONObject.toJSONString(param);
    }
}
