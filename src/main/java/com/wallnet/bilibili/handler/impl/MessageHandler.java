package com.wallnet.bilibili.handler.impl;

import com.wallnet.bilibili.handler.Handler;
import com.wallnet.bilibili.response.Danmu;
import com.wallnet.bilibili.response.TextDanmu;
import lombok.extern.slf4j.Slf4j;

import static com.wallnet.bilibili.common.enums.DanmuCmdEnums.TALK_MSG;

/**
 * @author skyli665
 * @date 2026-06-26 15:38
 */
@Slf4j
public class MessageHandler implements Handler {

    @Override
    public void handle(Long roomId, Danmu message) {
        TextDanmu danmu = new TextDanmu(message.getRaw());
        log.info("===>ts[{}],uid[{}]用户[{}]发送了弹幕:[{}]", danmu.getSendTime(), danmu.getUid(), danmu.getUname(), danmu.getMsg());
    }

    @Override
    public String getType() {
        return TALK_MSG.getCode();
    }
}
