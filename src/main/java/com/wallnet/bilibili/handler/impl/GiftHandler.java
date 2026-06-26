package com.wallnet.bilibili.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.wallnet.bilibili.common.enums.DanmuCmdEnums;
import com.wallnet.bilibili.handler.Handler;
import com.wallnet.bilibili.response.Danmu;
import com.wallnet.bilibili.response.GiftDanmu;
import lombok.extern.slf4j.Slf4j;

/**
 * @author skyli665
 * @date 2026-06-26 15:41
 */
@Slf4j
public class GiftHandler implements Handler {


    @Override
    public void handle(Long roomId, Danmu message) {
        GiftDanmu giftDanmu = JSON.parseObject(message.getRaw(), GiftDanmu.class);
        GiftDanmu.GiftData data = giftDanmu.getData();
        GiftDanmu.SenderUserInfo senderUinfo = data.getSenderUinfo();
        log.info("===>uid[{}]用户[{}]发送了礼物:[{}]", senderUinfo.getUid(), senderUinfo.getBase().getName(), data.getGiftName());
    }

    @Override
    public String getType() {
        return DanmuCmdEnums.SEND_GIFT.getCode();
    }
}
