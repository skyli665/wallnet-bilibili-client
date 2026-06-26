package com.wallnet.bilibili.response;

import lombok.Data;

/**
 * @author skyli665
 * @date 2025-06-22 21:56
 */
@Data
public class RoomUser {
    /**
     * 用户uid
     */
    private UserInfo uinfo;
    /**
     * 在舰时间
     */
    private Integer accompany;
    
    @Data
    public static class UserInfo {
        /**
         * uid
         */
        private Long uid;
        /**
         * 用户信息
         */
        private BiUserInfo base;
    }
}