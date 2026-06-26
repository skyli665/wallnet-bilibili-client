package com.wallnet.bilibili.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.wallnet.bilibili.common.enums.DanmuCmdEnums;
import com.wallnet.bilibili.handler.Handler;
import com.wallnet.bilibili.response.Danmu;
import com.wallnet.bilibili.response.SCDanmu;
import lombok.extern.slf4j.Slf4j;

/**
 * @author skyli665
 * @date 2026-06-26 15:44
 */
@Slf4j
public class ScHandler implements Handler {


    @Override
    public void handle(Long roomId, Danmu message) {
        SCDanmu danmu = JSON.parseObject(message.getRaw(), SCDanmu.class);
        log.info("===>uid[{}]用户[{}]发送了SC弹幕:[{}]", danmu.getUid(), danmu.getUsername(), danmu.getMessage());
    }

    @Override
    public String getType() {
        return DanmuCmdEnums.SUPER_CHAT_MESSAGE.getCode();
    }
}
