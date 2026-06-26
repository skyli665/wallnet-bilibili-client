package com.wallnet.bilibili.response;

import lombok.Data;

/**
 * @author skyli665
 * @date 2025-06-22 19:12
 */
@Data
public class RealName {
    
    private Integer status;
    /**
     * 驳回信息
     */
    private String remark;
    
    /**
     * 真实姓名
     *
     * @return
     */
    private String realname;
    /**
     * 身份证号码
     *
     * @return
     */
    private String card;
    /**
     * 身份证类型
     *
     * @return
     */
    private Integer cardType;
    
    public boolean isRealName() {
        return 1 == status;
    }
}
