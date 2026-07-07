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
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            OpenLiveDanmakuMessage msg = data.toJavaObject(OpenLiveDanmakuMessage.class);
            log.debug("解析后的弹幕: msg={}", msg.getMsg());
            log.info("[弹幕] {}: {}", msg.getUname(), msg.getMsg());
            msg.setRoomId(roomId);
            messageHandler.onDanmaku(msg);
        }
    },
    LIVE_OPEN_PLATFORM_SEND_GIFT("LIVE_OPEN_PLATFORM_SEND_GIFT", "收到礼物") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            OpenLiveGiftMessage msg = data.toJavaObject(OpenLiveGiftMessage.class);
            log.debug("解析后的礼物: giftName={}, giftNum={}", msg.getGiftName(), msg.getGiftNum());
            log.info("[礼物] {} 赠送 {}x{}", msg.getUname(), msg.getGiftName(), msg.getGiftNum());
            msg.setRoomId(roomId);
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
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            OpenLiveGuardMessage msg = data.toJavaObject(OpenLiveGuardMessage.class);
            if (msg.getUname() == null && msg.getUserInfo() != null) {
                msg.setUname(msg.getUserInfo().getUname());
                msg.setUface(msg.getUserInfo().getUface());
                msg.setUid(msg.getUserInfo().getUid());
                msg.setGuardLevelName(getGuardLevelName(msg.getGuardLevel()));
            }
            log.info("[舰长] {} 开通了{}{}{}", msg.getUname(), msg.getGuardLevelName(), msg.getGuardNum(), msg.getGuardUnit());
            msg.setRoomId(roomId);
            messageHandler.onGuard(msg);
        }
    },
    LIVE_OPEN_PLATFORM_SUPER_CHAT("LIVE_OPEN_PLATFORM_SUPER_CHAT", "醒目留言") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            OpenLiveSuperChatMessage msg = data.toJavaObject(OpenLiveSuperChatMessage.class);
            log.info("[醒目留言] {}: {}", msg.getUname(), msg.getMessage());
            msg.setRoomId(roomId);
            messageHandler.onSuperChat(msg);
        }
    },
    LIVE_OPEN_PLATFORM_SUPER_CHAT_DEL("LIVE_OPEN_PLATFORM_SUPER_CHAT_DEL", "删除醒目留言") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            Long messageId = data.getLong("message_id");
            log.info("[删除醒目留言] messageId: {}", messageId);
            messageHandler.onSuperChatDelete(messageId);
        }
    },
    LIVE_OPEN_PLATFORM_LIKE("LIVE_OPEN_PLATFORM_LIKE", "点赞") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            OpenLiveLikeMessage msg = data.toJavaObject(OpenLiveLikeMessage.class);
            log.debug("[点赞] {} 点赞了", msg.getUname());
            msg.setRoomId(roomId);
            messageHandler.onLike(msg);
        }
    },
    LIVE_OPEN_PLATFORM_INTERACTION_END("LIVE_OPEN_PLATFORM_INTERACTION_END", "由于异常停止推送") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            String gameId = data.getString("game_id");
            log.info("由于异常停止推送，一般是由于心跳过期或者主动调用END, gameId: {}", gameId);
            messageHandler.onLiveEnd();
        }
    },
    LIVE_OPEN_PLATFORM_LIVE_END("LIVE_OPEN_PLATFORM_LIVE_END", "直播结束") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            String gameId = data.getString("game_id");
            log.info("直播结束, gameId: {}", gameId);
            messageHandler.onLiveEnd();
        }
    },
    LIVE_OPEN_PLATFORM_LIVE_START("LIVE_OPEN_PLATFORM_LIVE_START", "直播开始") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            log.info("直播开始");
            messageHandler.onLiveStart();
        }
    },
    LIVE_OPEN_PLATFORM_LIVE_ROOM_ENTER("LIVE_OPEN_PLATFORM_LIVE_ROOM_ENTER", "用户进入直播间") {
        @Override
        public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
            printMessage(data);
            log.debug("用户进入直播间");
            messageHandler.onRoomEnter();
        }
    },
    ;


    private final String code;
    private final String name;

    public static OpenLiveCmdEnums getByCode(Object code) {
        EnumSet<OpenLiveCmdEnums> enums = EnumSet.allOf(OpenLiveCmdEnums.class);
        return enums.stream().filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public boolean codeEquals(Object code) {
        return this.getCode().equals(code);
    }

    public void handle(OpenLiveMessageHandler messageHandler, Long roomId, JSONObject data) {
        printMessage(data);
    }

    protected void printMessage(JSONObject data) {
        log.debug("弹幕消息原始数据: {}", data.toJSONString());
    }
}
