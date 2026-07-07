package com.wallnet.bilibili.response;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveLiveEndMessage extends Danmu {
    @JSONField(name = "room_id")
    private Long roomId;

    private String openId;

    private String unionId;

    private Long timestamp;

    @JSONField(name = "area_name")
    private String areaName;

    private String title;
}