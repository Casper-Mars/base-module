package org.r.base.common.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author casper
 * @date 19-10-24 下午2:51
 **/
public class SecurityUtils {


    /**
     * 获取证书信任管理器
     *
     * @return
     */
    public static X509TrustManager getDefaultTrustManager() {

        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }


}
