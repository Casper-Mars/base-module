package org.r.base.payment.service.impl.wechat;

import org.r.base.common.utils.XMLUtils;
import org.springframework.stereotype.Service;

/**
 * @author casper
 * @date 19-10-18 下午2:17
 **/
@Service("wechatScanPaymentPlugin")
public class WechatScanPaymentServiceImpl extends AbstractWechatServiceImpl {
    /**
     * 获取交易类型:app,native
     *
     * @return
     */
    @Override
    protected String getTradeType() {
        return "NATIVE";
    }

    /**
     * 封装返回值
     *
     * @param xml 请求结果
     */
    @Override
    protected String buildResult(XMLUtils xml) {
        return xml.getText(xml.getRootElement(), "code_url");
    }
}
