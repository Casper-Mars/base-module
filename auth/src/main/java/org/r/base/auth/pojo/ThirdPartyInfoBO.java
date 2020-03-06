package org.r.base.auth.pojo;

import lombok.Data;
import org.r.base.auth.enums.ThirdPartyTypeEnum;

/**
 * @author casper
 */
@Data
public class ThirdPartyInfoBO {


    /**
     * 三方登录类型
     */
    private ThirdPartyTypeEnum thirdPartyTypeEnum;
    /**
     * open id
     */
    private String openId;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * token
     */
    private String token;

    /**
     * 原始数据
     */
    private String metaData;


}
