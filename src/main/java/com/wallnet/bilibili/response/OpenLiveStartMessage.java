package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveStartMessage extends Danmu {
    @JSONField(name = "room_id")
    private Long roomId;
    @JSONField(name = "area_id")
    private Long areaId;
    private String title;
    private Long timestamp;
    @JSONField(name = "open_id")
    private String openId;
    @JSONField(name = "msg_id")
    private String msgId;
}