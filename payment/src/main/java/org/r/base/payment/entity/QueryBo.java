package org.r.base.payment.entity;

import lombok.Data;

/**
 * @author casper
 * @date 19-12-25 上午9:25
 **/
@Data
public class QueryBo {


    /**
     * 查询是否成功
     */
    private boolean isSuccess;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 账单数据
     */
    private String data;

    /**
     * 元数据
     */
    private String metaData;


    public static QueryBo fail() {
        return fail("");
    }

    public static QueryBo fail(String str) {
        return fail(str, "");
    }

    public static QueryBo fail(String msg, String metaData) {
        QueryBo queryBo = new QueryBo();
        queryBo.setSuccess(false);
        queryBo.setErrorMsg(msg);
        queryBo.setMetaData(metaData);
        return queryBo;
    }


}
