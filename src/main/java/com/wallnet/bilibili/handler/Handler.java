package com.wallnet.bilibili.handler;

import com.wallnet.bilibili.response.Danmu;

/**
 * @author skyli665
 * @date 2026-06-26 14:53
 */
public interface Handler {

    default void handle(Long roomId, Danmu message) {
        String str = String.format("%s处理%s房间消息:%s", getType(), roomId, message.getRaw());
        System.out.println(str);
    }

    String getType();

}
