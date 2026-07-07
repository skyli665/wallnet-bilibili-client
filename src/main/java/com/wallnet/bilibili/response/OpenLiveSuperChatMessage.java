package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class OpenLiveSuperChatMessage extends Danmu {
    @JSONField(name = "room_id")
    private Long roomId;

    private Long uid;
    private String uname;
    private String uface;
    private String message;

    @JSONField(name = "message_id")
    private Long messageId;

    private Long timestamp;
    private Integer price;

    @JSONField(name = "start_time")
    private Long startTime;

    @JSONField(name = "end_time")
    private Long endTime;

    @JSONField(name = "guard_level")
    private Integer guardLevel;

    @JSONField(name = "token_info")
    private List<TokenInfo> tokenInfo;

    @JSONField(name = "user_info")
    private UserInfo userInfo;

    @Data
    public static class TokenInfo {
        private String token;
    }

    @Data
    public static class UserInfo {
        private Long uid;
        private String uname;
        private String uface;
    }
}