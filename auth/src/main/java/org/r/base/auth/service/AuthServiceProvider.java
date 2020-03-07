package org.r.base.auth.service;

import org.r.base.auth.enums.ThirdPartyTypeEnum;
import org.r.base.auth.service.impl.QqAuthServiceImpl;
import org.r.base.auth.service.impl.WechatAuthServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author casper
 * @date 20-3-6 下午10:14
 **/
@Service
public class AuthServiceProvider implements ApplicationContextAware {

    private static ServiceHolder serviceHolder;


    private AuthServiceProvider() {
    }

    public static ThirdPartyAuthService get(ThirdPartyTypeEnum type) {
        switch (type) {
            case QQ:
                return serviceHolder.getQqAuthService();
            case WEIXIN:
                return serviceHolder.getWechatAuthService();
            default:
                return null;
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        serviceHolder = new ServiceHolder();
        serviceHolder.setQqAuthService((QqAuthServiceImpl) applicationContext.getBean("qqAuthServiceImpl"));
        serviceHolder.setWechatAuthService((WechatAuthServiceImpl) applicationContext.getBean("wechatAuthServiceImpl"));
    }

    private class ServiceHolder {

        private QqAuthServiceImpl qqAuthService;

        private WechatAuthServiceImpl wechatAuthService;

        QqAuthServiceImpl getQqAuthService() {
            return qqAuthService;
        }

        void setQqAuthService(QqAuthServiceImpl qqAuthService) {
            this.qqAuthService = qqAuthService;
        }

        WechatAuthServiceImpl getWechatAuthService() {
            return wechatAuthService;
        }

        void setWechatAuthService(WechatAuthServiceImpl wechatAuthService) {
            this.wechatAuthService = wechatAuthService;
        }
    }


}
