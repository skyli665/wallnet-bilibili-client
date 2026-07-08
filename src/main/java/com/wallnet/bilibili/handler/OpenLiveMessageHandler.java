package com.wallnet.bilibili.handler;

import com.wallnet.bilibili.response.*;

/**
 * 开放平台直播消息处理器接口
 */
public interface OpenLiveMessageHandler {

    /**
     * 收到弹幕消息
     */
    default void onDanmaku(OpenLiveDanmakuMessage message) {
    }

    /**
     * 收到礼物消息
     */
    default void onGift(OpenLiveGiftMessage message) {
    }

    /**
     * 收到舰长消息
     */
    default void onGuard(OpenLiveGuardMessage message) {
    }

    /**
     * 收到醒目留言消息
     */
    default void onSuperChat(OpenLiveSuperChatMessage message) {
    }

    /**
     * 收到点赞消息
     */
    default void onLike(OpenLiveLikeMessage message) {
    }

    /**
     * 收到删除醒目留言消息
     */
    default void onSuperChatDelete(Long messageId) {
    }

    /**
     * 直播开始
     */
    default void onLiveStart(OpenLiveStartMessage msg) {
    }

    /**
     * 直播结束
     */
    default void onLiveEnd(Danmu data) {
    }

    /**
     * 用户进入直播间
     */
    default void onRoomEnter(OpenLiveRoomEnterMessage msg) {
    }
}