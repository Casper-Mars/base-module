package org.r.base.payment.service.impl.wechat;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jdom.Element;
import org.r.base.common.enums.ProtocolEnum;
import org.r.base.common.enums.RequestMethodEnum;
import org.r.base.common.pojo.ProtocolProvider;
import org.r.base.common.pojo.RequestBo;
import org.r.base.common.pojo.RespondBo;
import org.r.base.common.servicce.HttpRequestService;
import org.r.base.common.utils.HttpUtils;
import org.r.base.payment.config.WechatPayConfig;
import org.r.base.payment.dto.NotifyDTO;
import org.r.base.payment.entity.PayCommon;
import org.r.base.payment.entity.QueryBo;
import org.r.base.payment.entity.QueryCommon;
import org.r.base.payment.entity.RefundCommon;
import org.r.base.payment.exception.PayException;
import org.r.base.payment.service.PaymentService;
import org.r.base.common.utils.SecurityUtils;
import org.r.base.common.utils.XDigestUtils;
import org.r.base.common.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author casper
 * @date 19-10-18 下午2:07
 **/
@Slf4j
public abstract class AbstractWechatServiceImpl implements PaymentService {

    @Autowired(required = false)
    protected WechatPayConfig wechatPayConfig;


    /**
     * 获取交易类型:app,native
     *
     * @return
     */
    protected abstract String getTradeType();

    /**
     * 构造请求参数
     * <xml>
     * <appid>wx2421b1c4370ec43b</appid>
     * <body>APP支付测试</body>
     * <mch_id>10000100</mch_id>
     * <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
     * <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
     * <out_trade_no>1415659990</out_trade_no>
     * <spbill_create_ip>14.23.150.211</spbill_create_ip>
     * <total_fee>1</total_fee>
     * <trade_type>APP</trade_type>
     * <sign>0CB01533B8C1EF103065174F50BCA001</sign>
     * </xml>
     */
    private String buildParam(PayCommon requestParam) {

        /*构造参数map，便于之后生成签名，使用treemap为了获得排序集合，满足微信支付文档的要求*/
        Map<String, String> param = getBaseParam();
        param.put("trade_type", getTradeType());
        param.put("spbill_create_ip", requestParam.getRemoteIp());
        param.put("body", requestParam.getBody());
        param.put("out_trade_no", requestParam.getOutTradeNo().getOutTradeNo());
        param.put("total_fee", String.valueOf(requestParam.getPayAmount().multiply(new BigDecimal(100)).intValue()));
        param.put("notify_url", requestParam.getNotifyUrl());
        return build0(param);
    }

    /**
     * 构建退款的参数
     *
     * @param refundCommon
     * @return
     */
    protected String buildRefundParam(RefundCommon refundCommon) {
        /*构造参数map，便于之后生成签名，使用treemap为了获得排序集合，满足微信支付文档的要求*/
        Map<String, String> param = getBaseParam();
        param.put("out_refund_no", refundCommon.getOutRefundNo());
        param.put("transaction_id", refundCommon.getTraceNo());
        param.put("refund_fee", String.valueOf(refundCommon.getRefundFee().multiply(new BigDecimal(100)).intValue()));
        param.put("total_fee", String.valueOf(refundCommon.getOrderFee().multiply(new BigDecimal(100)).intValue()));
        param.put("notify_url", refundCommon.getNotifyUrl());
        return build0(param);
    }


    private String build0(Map<String, String> param) {
        param.put("sign", this.sign(param, wechatPayConfig.getApiKey()));

        /*构造XML请求参数*/
        StringBuilder builder = new StringBuilder();
        builder.append("<xml>");
        for (Map.Entry<String, String> item : param.entrySet()) {
            builder.append("<").append(item.getKey()).append(">");
            builder.append(item.getValue());
            builder.append("</").append(item.getKey()).append(">");
        }
        builder.append("</xml>");
        return builder.toString();
    }


    /**
     * 封装返回值
     *
     * @param xml 请求结果
     */
    protected abstract String buildResult(XMLUtils xml);


    /**
     * 获取请求结果
     *
     * @param requestResult 请求结果
     * @return
     */
    protected String getResult(String requestResult) {
        /*解析返回的结果，从结果中获取prepayid和noncestr*/
        XMLUtils xml = new XMLUtils(requestResult);
        Element root = xml.getRootElement();
        /*判断xml解析是否成功，root为null说明解析失败*/
        if (root == null) {
            throw new RuntimeException("");
        }
        /*判断返回的结果*/
        boolean isError = false;
        String returnCode = xml.getText(root, "return_code");
        String errCode = xml.getText(root, "err_code");
        /*返回错误信息*/
        String errCodeDes = "";
        if ("FAIL".equals(returnCode)) {
            isError = true;
            errCodeDes = xml.getText(root, "return_msg");
        } else if (!StringUtils.isEmpty(errCode)) {
            isError = true;
            errCodeDes = xml.getText(root, "err_code_des");
        }
        if (isError) {
            throw new RuntimeException(String.format("errCode:%s,errCodeDes:%s", errCode, errCodeDes));
        }
        return buildResult(xml);
    }


    /**
     * 支付接口
     *
     * @param payCommon 支付参数，不同实现类参数不同
     * @return
     */
    @Override
    public String pay(PayCommon payCommon) throws PayException {
        String param = buildParam(payCommon);
        RespondBo respondBo = doRequest(param, wechatPayConfig.getGatewayUrl());
        return getResult((String) respondBo.getResult());
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
        /*------------获取返回的参数------------*/
        Map<String, String> params = getResultFromCallBack(request);

        /*------------校验签名------------*/
        /*获取返回的签名*/
        String retSign = params.get("sign");
        /*先把签名参数从参数集合中去除，为了计算签名*/
        params.remove("sign");
        /*计算签名*/
        String calSign = this.sign(params, wechatPayConfig.getApiKey());
        if (!calSign.equals(retSign)) {
            return NotifyDTO.fail();
        }

        /*------------校验返回的通信标记--------*/
        String returnCode = params.get("return_code");
        String returnMsg = params.get("return_msg");
        if (StringUtils.isEmpty(returnCode) || !"success".equalsIgnoreCase(returnCode)) {
            log.error("payment:" + billSn + " " + returnMsg);
            return NotifyDTO.fail();
        }

        /*-------------校验返回的业务结果---------------*/
        String resultCode = params.get("result_code");
        String err = params.get("err_code_des");
        if (StringUtils.isEmpty(resultCode) || !"success".equalsIgnoreCase(resultCode)) {
            log.error("payment:" + billSn + " " + err);
            return NotifyDTO.fail();
        }

        /*-------------校验返回的商户和appid---------------*/
        /*商户号*/
        String mchId = params.get("mch_id");
        /*公众账号id*/
        String appid = params.get("appid");
        /*微信移动支付的支付流水号*/
        String tradeNo = params.get("transaction_id");
        if (!StringUtils.isEmpty(mchId) && !StringUtils.isEmpty(appid) && appid.equals(wechatPayConfig.getAppId()) && mchId.equals(wechatPayConfig.getMchid())) {
            return new NotifyDTO(tradeNo, true, billSn, "");
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
    public Boolean refund(RefundCommon refundCommon) throws PayException {

        String param = buildRefundParam(refundCommon);
        RespondBo respondBo = doRequest(param, wechatPayConfig.getRefundUrl());
        String result = (String) respondBo.getResult();
        /*检查返回值*/
        Map<String, String> resultParam = getParam(result);
        String returnCode = resultParam.get("return_code");
        if (StringUtils.isEmpty(returnCode) || "FAIL".equalsIgnoreCase(returnCode)) {
            log.error(String.format("wechat refund fail:%s", resultParam.get("return_msg")));
            return false;
        }
        String resultCode = resultParam.get("result_code");
        if (StringUtils.isEmpty(resultCode) || "FAIL".equalsIgnoreCase(resultCode)) {
            log.error(String.format("wechat refund fail,err_code:%s,err_desc:%s", resultParam.get("err_code"), resultParam.get("err_code_des")));
            return false;
        }
        return true;
    }


    private RespondBo doRequest(String param, String url) throws PayException {
        RequestBo requestBo = new RequestBo();
        requestBo.setParam(param);
        requestBo.setProtocol(getPayProtocol());

        RequestMethodEnum requestMethodEnum = RequestMethodEnum.of(wechatPayConfig.getRequestMethod());
        if (requestMethodEnum == null) {
            throw new PayException("请求方法不存在，请查看微信支付的信息是否配置正确");
        }
        requestBo.setMethod(RequestMethodEnum.of(wechatPayConfig.getRequestMethod()));
        requestBo.setUrl(url);
        return HttpUtils.doRequest(requestBo);
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

        Map<String, String> params = getResultFromCallBack(request);
        String isSusscess = params.get("return_code");
        if (StringUtils.isEmpty(isSusscess) || "FAIL".equalsIgnoreCase(isSusscess)) {
            return NotifyDTO.fail(params.get("return_msg"));
        }

        /*获取加密串A*/
        String reqInfo = params.get("reqInfo");
        log.info(String.format("获取到的加密信息:%s", reqInfo));
        try {
            /*对加密串A做base64解码，得到加密串B*/
            String encryStrB = new String(XDigestUtils.decryptBASE64(reqInfo));
            /*对商户key做md5，得到32位小写keyB*/
            String keyB = XDigestUtils.md5Hex(wechatPayConfig.getApiKey());
            /*用key*对加密串B做AES-256-ECB解密（PKCS7Padding）*/
            String info = XDigestUtils.aes256EcbPKCS7PaddingDecrypt(encryStrB, keyB);
            /*提取参数*/
            Map<String, String> param = getParam(info);
            String refundStatus = param.get("refund_status");
            if (StringUtils.isEmpty(refundStatus) || !"SUCCESS".equalsIgnoreCase(refundStatus)) {
                return NotifyDTO.fail();
            }
            String outTradeNo = param.get("outTradeNo");
            if (StringUtils.isEmpty(outTradeNo) || !outTradeNo.equals(billSn)) {
                return NotifyDTO.fail();
            }
            String transactionId = param.get("transactionId");
            if (StringUtils.isEmpty(transactionId)) {
                return NotifyDTO.fail();
            }
            return new NotifyDTO(transactionId, true, outTradeNo, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NotifyDTO.fail();
    }

    /**
     * 查询账单
     *
     * @param queryCommon 查询参数
     * @return
     */
    @Override
    public QueryBo query(QueryCommon queryCommon) throws PayException {
        Map<String, String> param = getBaseParam();
        param.put("transaction_id", queryCommon.getTradeNo());
        String s = build0(param);
        RespondBo respondBo;
        try {
            respondBo = doRequest(s, wechatPayConfig.getQueryUrl());
            String result = (String) respondBo.getResult();
            /*检查返回值*/
            Map<String, String> resultParam = getParam(result);
            String returnCode = resultParam.get("return_code");
            if (StringUtils.isEmpty(returnCode) || "FAIL".equalsIgnoreCase(returnCode)) {
                log.error(String.format("wechat refund fail:%s", resultParam.get("return_msg")));
                return QueryBo.fail(resultParam.get("return_msg"), result);
            }
            String resultCode = resultParam.get("result_code");
            if (StringUtils.isEmpty(resultCode) || "FAIL".equalsIgnoreCase(resultCode)) {
                log.error(String.format("wechat refund fail,err_code:%s,err_desc:%s", resultParam.get("err_code"), resultParam.get("err_code_des")));
                return QueryBo.fail(resultParam.get("err_code_des"), result);
            }
            QueryBo queryBo = new QueryBo();
            queryBo.setMetaData(result);
            queryBo.setSuccess(true);
            queryBo.setData(JSONObject.toJSONString(resultParam));
            return queryBo;
        } catch (PayException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * MD5签名
     *
     * @param content
     * @param key
     * @return
     */
    protected String sign(Map<String, String> content, String key) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> item : content.entrySet()) {
            String k = item.getKey();
            String v = item.getValue();
            if (StringUtils.isEmpty(v)) {
                continue;
            }
            str.append(k).append("=").append(v).append("&");
        }
        str.append("key=").append(key);
        return XDigestUtils.md5Hex(str.toString()).toUpperCase();
    }

    /**
     * 商户生成的随机字符串 字符串类型，32个字节以下
     *
     * @return
     */
    protected String createNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res.append(chars.charAt(rd.nextInt(chars.length() - 1)));
        }
        return res.toString();
    }


    /**
     * 获取回调的返回的参数
     *
     * @param request 请求信息
     * @return
     */
    protected Map<String, String> getResultFromCallBack(HttpServletRequest request) {
        /*------------获取返回的参数------------*/
        String tmp = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            while ((tmp = reader.readLine()) != null) {
                sb.append(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getParam(sb.toString());
    }

    /**
     * 从xml字符串转化成map
     *
     * @param result
     * @return
     */
    protected Map<String, String> getParam(String result) {
        Map<String, String> params = new TreeMap<>(String::compareToIgnoreCase);
        XMLUtils xmlUtils = new XMLUtils(result);
        Map<String, Object> stringObjectMap = xmlUtils.toMap();
        stringObjectMap.forEach((k, v) -> params.put(k, (String) v));
        return params;
    }


    /**
     * 获取支付的协议信息
     *
     * @return
     */
    private ProtocolProvider getPayProtocol() {
        return new ProtocolProvider() {
            @Override
            public ProtocolEnum getProtocolType() {
                return ProtocolEnum.https;
            }

            @Override
            public SSLContext getSslContext() throws NoSuchAlgorithmException {
                return SSLContext.getInstance("TLS");
            }

            /**
             * @return 秘钥管理器, 如果没有证书返回null, 微信支付不需要证书, 此时返回null不会影响后面的逻辑
             * @throws KeyStoreException
             */
            @Override
            public KeyManager getKeyManager() throws KeyStoreException {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                try {
                    keyStore.load(new FileInputStream(wechatPayConfig.getP12CertPath()), wechatPayConfig.getMchid().toCharArray());
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(keyStore, wechatPayConfig.getMchid().toCharArray());
                    return kmf.getKeyManagers()[0];
                } catch (IOException e) {
//                    e.printStackTrace();
                } catch (NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public X509TrustManager getTrustManager() {
                return SecurityUtils.getDefaultTrustManager();
            }
        };
    }


    /**
     * 封装基础的参数
     *
     * @return
     */
    private Map<String, String> getBaseParam() {
        Map<String, String> param = new TreeMap<>(String::compareToIgnoreCase);
        param.put("appid", wechatPayConfig.getAppId());
        param.put("mch_id", wechatPayConfig.getMchid());
        param.put("nonce_str", this.createNoncestr());
        return param;
    }


}
