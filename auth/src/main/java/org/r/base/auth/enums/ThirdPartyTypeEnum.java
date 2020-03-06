package org.r.base.auth.enums;

/**
 * @author casper
 * @date 20-3-6 下午4:02
 **/
public enum ThirdPartyTypeEnum {


    /**
     * 三方登录类型
     */
    WEIXIN(1, "微信"),
    QQ(2, "QQ"),
    WEIBO(3, "微博"),
    ;

    /**
     * 第三方类型
     */
    private int code;


    /**
     * 第三方名称
     */
    private String name;


    ThirdPartyTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getType() {
        return code;
    }

    public String getName() {
        return name;
    }
}
