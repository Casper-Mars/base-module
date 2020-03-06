package org.r.base.auth.service.impl;

import org.r.base.auth.config.QqConfig;
import org.r.base.auth.pojo.ThirdPartyInfoBO;
import org.r.base.auth.service.ThirdPartyAuthService;
import org.r.base.common.enums.ProtocolEnum;
import org.r.base.common.enums.RequestMethodEnum;
import org.r.base.common.pojo.ProtocolProvider;
import org.r.base.common.pojo.RequestBo;
import org.r.base.common.pojo.RespondBo;
import org.r.base.common.servicce.HttpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author casper
 * @date 20-3-6 下午4:07
 **/
@Service
public class QqAuthServiceImpl implements ThirdPartyAuthService {


    @Autowired
    private QqConfig qqconfig;

    @Autowired
    private HttpRequestService httpRequestService;

    /**
     * 用授权码执行第三方登录操作
     *
     * @param code 授权码
     * @return
     */
    @Override
    public ThirdPartyInfoBO login(String code) {

        RequestBo requestBo = new RequestBo();
        requestBo.setUrl(getTokenUrl(code));
        requestBo.setMethod(RequestMethodEnum.GET);
        requestBo.setProtocol(new ProtocolProvider() {
            @Override
            public ProtocolEnum getProtocolType() {
                return ProtocolEnum.http;
            }

            @Override
            public SSLContext getSslContext() throws NoSuchAlgorithmException {
                return null;
            }

            @Override
            public KeyManager getKeyManager() throws KeyStoreException {
                return null;
            }

            @Override
            public X509TrustManager getTrustManager() {
                return null;
            }
        });
        RespondBo respondBo = httpRequestService.doRequest(requestBo);
        System.out.println(respondBo);
        return null;
    }


    private String getTokenUrl(String code) {
        StringBuilder sb = new StringBuilder();

        sb.append(qqconfig.getAuthUrl())
                .append("?")
                .append("client_id=").append(qqconfig.getClientId()).append("&")
                .append("client_secret=").append(qqconfig.getClientSecret()).append("&")
                .append("code=").append(code).append("&")
                .append("grant_type=").append(qqconfig.getGrantType()).append("&")
                .append("redirect_uri=").append(qqconfig.getRedirectUri());
        return sb.toString();
    }


}
