package org.r.base.common.enums;

/**
 * @author casper
 * @date 19-10-16 下午2:12
 **/
public enum ProtocolEnum {

    /**
     * 协议
     */
    http("http"),
    https("https");

    private String name;

    ProtocolEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProtocolEnum of(String name) {
        for (ProtocolEnum value : ProtocolEnum.values()) {
            if (value.name.toLowerCase().equals(name)) {
                return value;
            }
        }
        return null;
    }


}
