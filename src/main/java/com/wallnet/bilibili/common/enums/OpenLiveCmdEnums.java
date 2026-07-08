package com.wallnet.bilibili.common.enums;

import com.alibaba.fastjson2.JSONObject;
import com.wallnet.bilibili.handler.OpenLiveMessageHandler;
import com.wallnet.bilibili.response.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;

/**
 * @author skyli665
 * @date 2026-07-07 20:48
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum OpenLiveCmdEnums {

    LIVE_OPEN_PLATFORM_DM("LIVE_OPEN_PLATFORM_DM", "发送弹幕") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            OpenLiveDanmakuMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveDanmakuMessage.class);
            log.debug("解析后的弹幕: msg={}", msg.getMsg());
            log.info("[弹幕] {}: {}", msg.getUname(), msg.getMsg());
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            messageHandler.onDanmaku(msg);
        }
    },
    LIVE_OPEN_PLATFORM_SEND_GIFT("LIVE_OPEN_PLATFORM_SEND_GIFT", "收到礼物") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            OpenLiveGiftMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveGiftMessage.class);
            log.debug("解析后的礼物: giftName={}, giftNum={}", msg.getGiftName(), msg.getGiftNum());
            log.info("[礼物] {} 赠送 {}x{}", msg.getUname(), msg.getGiftName(), msg.getGiftNum());
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            messageHandler.onGift(msg);
        }
    },
    LIVE_OPEN_PLATFORM_GUARD("LIVE_OPEN_PLATFORM_GUARD", "开通舰长") {
        private String getGuardLevelName(int level) {
            switch (level) {
                case 1:
                    return "总督";
                case 2:
                    return "提督";
                case 3:
                    return "舰长";
                default:
                    return "Lv." + level;
            }
        }

        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            OpenLiveGuardMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveGuardMessage.class);
            if (msg.getUname() == null && msg.getUserInfo() != null) {
                msg.setUname(msg.getUserInfo().getUname());
                msg.setUface(msg.getUserInfo().getUface());
                msg.setUid(msg.getUserInfo().getUid());
                msg.setGuardLevelName(getGuardLevelName(msg.getGuardLevel()));
            }
            log.info("[舰长] {} 开通了{}{}{}", msg.getUname(), msg.getGuardLevelName(), msg.getGuardNum(), msg.getGuardUnit());
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            messageHandler.onGuard(msg);
        }
    },
    LIVE_OPEN_PLATFORM_SUPER_CHAT("LIVE_OPEN_PLATFORM_SUPER_CHAT", "醒目留言") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            OpenLiveSuperChatMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveSuperChatMessage.class);
            log.info("[醒目留言] {}: {}", msg.getUname(), msg.getMessage());
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            messageHandler.onSuperChat(msg);
        }
    },
    LIVE_OPEN_PLATFORM_SUPER_CHAT_DEL("LIVE_OPEN_PLATFORM_SUPER_CHAT_DEL", "删除醒目留言") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            Long messageId = JSONObject.parseObject(data.getRaw()).getLong("message_id");
            log.info("[删除醒目留言] messageId: {}", messageId);
            messageHandler.onSuperChatDelete(messageId);
        }
    },
    LIVE_OPEN_PLATFORM_LIKE("LIVE_OPEN_PLATFORM_LIKE", "点赞") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            OpenLiveLikeMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveLikeMessage.class);
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            log.debug("[点赞] {} 点赞了", msg.getUname());
            messageHandler.onLike(msg);
        }
    },
    LIVE_OPEN_PLATFORM_INTERACTION_END("LIVE_OPEN_PLATFORM_INTERACTION_END", "由于异常停止推送") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            String gameId = JSONObject.parseObject(data.getRaw()).getString("game_id");
            log.info("由于异常停止推送，一般是由于心跳过期或者主动调用END, gameId: {}", gameId);
            data.setGameId(gameId);
            data.setRoomId(data.getRoomId());
            data.setRaw(data.getRaw());
            messageHandler.onLiveEnd(data);
        }
    },
    LIVE_OPEN_PLATFORM_LIVE_END("LIVE_OPEN_PLATFORM_LIVE_END", "直播结束") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            String gameId = JSONObject.parseObject(data.getRaw()).getString("game_id");
            log.info("直播结束, gameId: {}", gameId);
            data.setGameId(gameId);
            data.setRoomId(data.getRoomId());
            data.setRaw(data.getRaw());
            messageHandler.onLiveEnd(data);
        }
    },
    LIVE_OPEN_PLATFORM_LIVE_START("LIVE_OPEN_PLATFORM_LIVE_START", "直播开始") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            log.info("直播开始");
            OpenLiveStartMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveStartMessage.class);
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            messageHandler.onLiveStart(msg);
        }
    },
    LIVE_OPEN_PLATFORM_LIVE_ROOM_ENTER("LIVE_OPEN_PLATFORM_LIVE_ROOM_ENTER", "用户进入直播间") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
            printMessage(data.getRaw());
            log.debug("用户进入直播间");
            OpenLiveRoomEnterMessage msg = JSONObject.parseObject(data.getRaw(), OpenLiveRoomEnterMessage.class);
            msg.setRaw(data.getRaw());
            msg.setRoomId(data.getRoomId());
            messageHandler.onRoomEnter(msg);
        }
    },
    ;


    private final String code;
    private final String name;

    public static OpenLiveCmdEnums getByCode(Object code) {
        EnumSet<OpenLiveCmdEnums> enums = EnumSet.allOf(OpenLiveCmdEnums.class);
        return enums.stream().filter(e -> e.codeEquals(code))
                .findFirst()
                .orElse(null);
    }

    public boolean codeEquals(Object code) {
        return this.getCode().equals(code);
    }

    public void handle(OpenLiveMessageHandler messageHandler, Danmu data) {
    }

    protected void printMessage(String raw) {
        log.debug("弹幕消息原始数据: {}", raw);
    }
}
