package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author skyli665
 * @date 2026-01-20 23:54
 */
@Data
public class LiveUserInfo {
    
    private Info info;
    /**
     * 扩展信息
     */
    private Exp exp;
    
    @Data
    public static class Info {
        private Long uid;
        private String uname;
        private String face;
        /**
         * 认证信息
         */
        @JSONField(name = "official_verify")
        private OfficialVerify officialVerify;
        private Integer gender;
    }
    
    @Data
    public static class OfficialVerify {
        /**
         * 认证类型
         */
        private Integer type;
        /**
         * 认证信息
         */
        private String desc;
    }
    
    @Data
    public static class Exp {
        /**
         * 主播等级
         */
        @JSONField(name = "master_level")
        private MasterLevel masterLevel;
        /**
         * 粉丝数
         */
        @JSONField(name = "follower_num")
        private Integer followerNum;
        /**
         * 直播间id
         */
        @JSONField(name = "room_id")
        private Integer roomId;
        /**
         * 粉丝牌名
         */
        @JSONField(name = "medal_name")
        private String medalName;
        @JSONField(name = "glory_count")
        private Integer gloryCount;
        private String pendant;
        @JSONField(name = "link_group_num")
        private Integer linkGroupNum;
        /**
         * 直播间公告
         */
        @JSONField(name = "room_news")
        private RoomNews roomNews;
    }
    
    @Data
    public static class MasterLevel {
        private Integer level;
        private Integer color;
        private Long[] current;
        private Long[] next;
    }
    
    @Data
    public static class RoomNews {
        private String content;
        private LocalDateTime ctime;
        @JSONField(name = "ctime_text", format = "yyyy-MM-dd")
        private LocalDate ctimeText;
    }
}
