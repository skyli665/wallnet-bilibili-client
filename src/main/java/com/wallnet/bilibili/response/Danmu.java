package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 弹幕消息实体类
 * 根据B站弹幕协议结构定义
 *
 * @author skyli665
 * @date 2026-01-24 23:30
 */
@Data
public class Danmu {

    private String cmd;

    @JSONField(name = "dm_v2")
    private String dmV2;

    private List<Object> info;

    private Long sendTime;

    private String raw;

    public LocalDateTime getSendTime() {
        // 时间戳转为LocalDateTime
        Instant instant = Instant.ofEpochMilli(sendTime);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}