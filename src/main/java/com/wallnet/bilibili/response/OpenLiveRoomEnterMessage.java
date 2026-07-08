package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveRoomEnterMessage extends Danmu {
    @JSONField(name = "room_id")
    private Long roomId;

    private String uname;
    private String uface;
    private Long timestamp;
    @JSONField(name = "open_id")
    private String openId;
    @JSONField(name = "msg_id")
    private String msgId;
}