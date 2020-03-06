package org.r.base.common.enums;

/**
 * @author casper
 * @date 19-10-16 下午2:09
 **/
public enum RequestMethodEnum {

    /**
     * http请求方法
     */
    GET("get"),
    POST("post"),
    PUT("put"),
    DELETE("delete");

    private String name;

    RequestMethodEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RequestMethodEnum of(String name) {
        name = name.toLowerCase();
        for (RequestMethodEnum value : RequestMethodEnum.values()) {
            if (value.name.toLowerCase().equals(name)) {
                return value;
            }
        }
        return null;
    }


}
