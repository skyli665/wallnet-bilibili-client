package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author skyli665
 * @date 2026-01-24 23:49
 */
@Slf4j
@Data
public class TextDanmu extends Danmu {

    // 弹幕显示模式（1:滚动、4:底部、5:顶部）
    private Integer mode;
    private Long uid;
    private String uname;
    private String msg;
    // 是否是管理员
    private Boolean isAdmin;

    public TextDanmu(String raw) {
        JSONObject json = JSON.parseObject(raw);
        JSONArray info = json.getJSONArray("info");
        mode = info.getJSONArray(0).getInteger(1);
        uid = info.getJSONArray(2).getLong(0);
        uname = info.getJSONArray(2).getString(1);
        msg = info.getString(1);
        isAdmin = info.getJSONArray(2).getBoolean(2);
        long ts = info.getJSONArray(0).getLong(4);
        super.setSendTime(ts);
    }

}