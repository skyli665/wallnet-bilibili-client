package com.wallnet.bilibili.response;

import com.wallnet.bilibili.common.enums.GenderEnums;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author skyli665
 * @date 2025-06-22 18:55
 */
@Data
public class BiUserInfo {
    /**
     * 生日
     */
    private Long birthday;
    /**
     * uid
     */
    private Long mid;
    /**
     * 昵称
     */
    private String name;
    /**
     * 性别
     */
    private String sex;
    /**
     * 头像
     */
    private String face;
    
    private Official official;
    
    public Long getUid() {
        return mid;
    }
    
    public LocalDateTime getBirthday() {
        // 把时间戳转换为LocalDateTime
        return LocalDateTime.ofEpochSecond(birthday, 0, ZoneOffset.ofHours(8));
    }
    
    public GenderEnums getGender() {
        return GenderEnums.getByCode(sex);
    }
    
    public class Official {
        private Integer role;
        private String title;
        private String desc;
        private Integer type;
    }
    
    
}
