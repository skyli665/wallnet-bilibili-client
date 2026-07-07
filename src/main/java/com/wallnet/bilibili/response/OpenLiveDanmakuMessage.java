package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveDanmakuMessage extends Danmu {
    private String uname;
    private Long uid;
    private String uface;
    private Long timestamp;

    @JSONField(name = "room_id")
    private Long roomId;

    private String msg;

    @JSONField(name = "msg_id")
    private String msgId;

    @JSONField(name = "guard_level")
    private Integer guardLevel;

    @JSONField(name = "fans_medal_wearing_status")
    private Boolean fansMedalWearingStatus;

    @JSONField(name = "fans_medal_name")
    private String fansMedalName;

    @JSONField(name = "fans_medal_level")
    private Integer fansMedalLevel;

    @JSONField(name = "emoji_img_url")
    private String emojiImgUrl;

    @JSONField(name = "dm_type")
    private Integer dmType;
}