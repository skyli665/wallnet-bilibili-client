package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveLikeMessage extends Danmu {
    @JSONField(name = "room_id")
    private Long roomId;

    private Long uid;
    private String uname;
    private String uface;
    private Long timestamp;
    private Integer count;

    @JSONField(name = "guard_level")
    private Integer guardLevel;
}