package org.r.base.auth.service;

import org.r.base.auth.pojo.ThirdPartyInfoBO;

/**
 * @author casper
 * @date 20-3-6 下午4:01
 **/
public interface ThirdPartyAuthService {



    /**
     * 用授权码执行第三方登录操作
     *
     * @param code 授权码
     * @return
     */
    ThirdPartyInfoBO login(String code);

}
