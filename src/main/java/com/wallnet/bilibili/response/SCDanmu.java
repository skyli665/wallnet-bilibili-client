package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author skyli665
 * @date 2026-06-26 11:20
 */
@Data
public class SCDanmu extends Danmu {

    @JSONField(name = "msg_id")
    private String msgId;

    private ScData data;


    @Data
    public class ScData {
        private Gift gift;
        private String message;
        private Integer price;
        private Integer rate;
        @JSONField(name = "start_time")
        private Long startTime;
        private Integer time;
        private Long uid;
        @JSONField(name = "user_info")
        private UserInfo userInfo;
    }

    @Data
    public class Gift {
        @JSONField(name = "gift_id")
        private Integer giftId;
        @JSONField(name = "gift_name")
        private String giftName;
        private Integer num;
    }

    @Data
    public class UserInfo {
        @JSONField(name = "face")
        private String face;
        @JSONField(name = "face_frame")
        private String faceFrame;
        @JSONField(name = "guard_level")
        private Integer guardLevel;
        @JSONField(name = "is_main_vip")
        private Integer isMainVip;
        private Integer manager;
        private String title;
        private String uname;
        @JSONField(name = "user_level")
        private Integer userLevel;
    }

    public String getMessage() {
        return data.getMessage();
    }

    public String getUsername() {
        return data.getUserInfo().getUname();
    }

    public Long getUid() {
        return data.getUid();
    }
}
