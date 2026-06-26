package com.wallnet.bilibili.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author skyli665
 * @date 2026-01-21 14:26
 */
@Getter
@AllArgsConstructor
public enum LiveStatusEnum {
    
    NOT_LIVE(0, "未开播"),
    LIVING(1, "直播中"),
    RECORDING(2, "回放"),
    ;
    
    private final Integer code;
    private final String desc;
    
    public boolean codeEquals(Object code) {
        return this.code.equals(code);
    }
}
