package com.wallnet.bilibili.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author skyli665
 * @date 2025-10-19 15:52
 */
@AllArgsConstructor
@Getter
public enum GenderEnums {
    
    MALE("男"),
    FEMALE("女"),
    UNKNOWN("未知");
    
    private final String code;
    
    public static GenderEnums getByCode(String code) {
        for (GenderEnums value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
