package org.r.base.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.r.base.auth.config.WechatConfig;
import org.r.base.auth.enums.ThirdPartyTypeEnum;
import org.r.base.auth.pojo.ThirdPartyInfoBO;
import org.r.base.auth.service.ThirdPartyAuthService;
import org.r.base.common.enums.RequestMethodEnum;
import org.r.base.common.pojo.RequestBo;
import org.r.base.common.pojo.RespondBo;
import org.r.base.common.servicce.HttpRequestService;
import org.r.base.common.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author casper
 * @date 20-3-6 下午5:03
 **/
@Service
public class WechatAuthServiceImpl implements ThirdPartyAuthService {


    @Autowired
    private WechatConfig wechatConfig;


    /**
     * 用授权码执行第三方登录操作
     *
     * @param code 授权码
     * @return
     */
    @Override
    public ThirdPartyInfoBO login(String code) {
        ThirdPartyInfoBO thirdPartyInfoBO = new ThirdPartyInfoBO();
        thirdPartyInfoBO.setThirdPartyTypeEnum(ThirdPartyTypeEnum.WEIXIN);
        /*请求授权*/
        RequestBo requestBo = new RequestBo();
        requestBo.setUrl(getTokenUrl(code));
        requestBo.setMethod(RequestMethodEnum.GET);
        RespondBo respondBo = HttpUtils.doRequest(requestBo);
        System.out.println(respondBo);

        JSONObject jsonObject = JSONObject.parseObject((String) respondBo.getResult());
        thirdPartyInfoBO.setOpenId(jsonObject.getString("openid"));
        thirdPartyInfoBO.setToken(jsonObject.getString("access_token"));
        thirdPartyInfoBO.setMetaData((String) respondBo.getResult());
        if (StringUtils.isEmpty(thirdPartyInfoBO.getOpenId()) || StringUtils.isEmpty(thirdPartyInfoBO.getToken())) {
            return thirdPartyInfoBO;
        }
        /*获取用户信息*/
        requestBo.setUrl(getUserInfoUrl(thirdPartyInfoBO.getToken(), thirdPartyInfoBO.getOpenId()));
        RespondBo info = HttpUtils.doRequest(requestBo);
        JSONObject infoObj = JSONObject.parseObject((String) info.getResult());
        thirdPartyInfoBO.setAvatar(infoObj.getString("headimgurl"));
        thirdPartyInfoBO.setNickname(infoObj.getString("nickname"));

        return thirdPartyInfoBO;
    }


    private String getTokenUrl(String code) {
        StringBuilder sb = new StringBuilder();

        sb.append(wechatConfig.getAuthUrl())
                .append("?")
                .append("appid=").append(wechatConfig.getAppId()).append("&")
                .append("secret=").append(wechatConfig.getSecret()).append("&")
                .append("code=").append(code).append("&")
                .append("grant_type=").append(wechatConfig.getGrantType());
        return sb.toString();
    }

    private String getUserInfoUrl(String token, String openId) {
        StringBuilder sb = new StringBuilder();

        sb.append("https://api.weixin.qq.com/sns/userinfo?access_token=").append(token)
                .append("&openid=").append(openId);
        return sb.toString();
    }

}
