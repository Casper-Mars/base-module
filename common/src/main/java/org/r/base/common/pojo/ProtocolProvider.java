package org.r.base.common.pojo;

import org.r.base.common.enums.ProtocolEnum;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author casper
 * @date 19-10-24 下午2:22
 **/
public interface ProtocolProvider {


    /**
     * 获取协议类型
     *
     * @return 协议类型
     */
    ProtocolEnum getProtocolType();

    /**
     * 获取ssl上下文，https协议会用到
     *
     * @return 上下文
     */
    SSLContext getSslContext() throws NoSuchAlgorithmException;

    /**
     * 获取秘钥管理器，
     *
     * @return
     */
    KeyManager getKeyManager() throws KeyStoreException;

    /**
     * 获取证书管理器
     *
     * @return
     */
    X509TrustManager getTrustManager();


}
