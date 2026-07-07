package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveGuardMessage extends Danmu {
    @JSONField(name = "room_id")
    private Long roomId;

    private Long uid;
    private String uname;
    private String uface;

    @JSONField(name = "guard_level")
    private Integer guardLevel;

    @JSONField(name = "guard_num")
    private Integer guardNum;

    @JSONField(name = "guard_unit")
    private String guardUnit;

    @JSONField(name = "guard_type")
    private Integer guardType;

    private String guardLevelName;

    private Long timestamp;

    @JSONField(name = "anchor_info")
    private AnchorInfo anchorInfo;

    @JSONField(name = "user_info")
    private UserInfo userInfo;

    @JSONField(name = "msg_id")
    private String msgId;

    @Data
    public static class AnchorInfo {
        private Long uid;
        private String uname;
        private String uface;
    }

    @Data
    public static class UserInfo {
        private Long uid;
        private String uname;
        private String uface;
    }
}