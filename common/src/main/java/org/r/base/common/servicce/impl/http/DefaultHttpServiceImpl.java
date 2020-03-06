package org.r.base.common.servicce.impl.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.r.base.common.enums.ProtocolEnum;
import org.r.base.common.enums.RequestMethodEnum;
import org.r.base.common.pojo.ProtocolProvider;
import org.r.base.common.pojo.RequestBo;
import org.r.base.common.pojo.RespondBo;
import org.r.base.common.servicce.HttpRequestService;
import org.r.base.common.servicce.HttpRequestStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author casper
 * @date 19-10-16 下午3:02
 **/
@Service
@Slf4j
public class DefaultHttpServiceImpl implements HttpRequestService {


    @Autowired
    private PostHttpRequestStrategy postHttpRequestStrategy;
    @Autowired
    private GetHttpRequestStrategy getHttpRequestStrategy;


    /**
     * 获取请求处理单元
     *
     * @param requestMethod 请求方法
     * @return
     */
    @Override
    public HttpRequestStrategy getStrategy(RequestMethodEnum requestMethod) {
        HttpRequestStrategy tmp = null;
        switch (requestMethod) {
            case GET:
                tmp = getHttpRequestStrategy;
                break;
            case POST:
                tmp = postHttpRequestStrategy;
                break;
            case DELETE:
                break;
            case PUT:
                break;
            default:
                return null;
        }
        return tmp;
    }

    /**
     * 执行请求
     *
     * @param requestBo 请求信息
     * @return
     */
    @Override
    public RespondBo doRequest(RequestBo requestBo) {

        HttpRequestStrategy strategy = getStrategy(requestBo.getMethod());
        if (strategy == null) {
            throw new RuntimeException("不支持的请求方法");
        }
        OkHttpClient okHttpClient = buildClient(requestBo.getProtocol());
        Request request = strategy.buildRequest(requestBo);
        RespondBo respondBo = new RespondBo();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            ResponseBody body = execute.body();
            respondBo.setSuccess(execute.isSuccessful() && body != null);
            if (respondBo.getSuccess()) {
                respondBo.setResult(body.string());
            }
        } catch (IOException e) {
            e.printStackTrace();
            respondBo.setSuccess(false);
        }
        return respondBo;
    }


    /**
     * 根据协议构建请求客户端
     *
     * @param protocol 协议
     * @return 客户端
     */
    private OkHttpClient buildClient(ProtocolProvider protocol) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (protocol != null) {
            ProtocolEnum protocolType = protocol.getProtocolType();
            switch (protocolType) {
                default:
                case http:
                    break;
                case https:
                    try {
                        X509TrustManager trustManager = protocol.getTrustManager();
                        SSLContext sslContext = protocol.getSslContext();
                        KeyManager keyManager = protocol.getKeyManager();
                        sslContext.init(new KeyManager[]{keyManager}, new TrustManager[]{trustManager}, null);
                        builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                        log.error("can not creat ssl context.KeyManagement off");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        log.error("can not creat ssl context.Missing tls algorithm");
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return builder.build();
    }

}
