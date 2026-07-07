package com.wallnet.bilibili.common.enums;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wallnet.bilibili.handler.OpenLiveMessageHandler;
import com.wallnet.bilibili.response.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

@Slf4j
@Getter
@AllArgsConstructor
public enum DanmuCmdEnums {

    TALK_MSG("DANMU_MSG", "弹幕消息") {
        @Override
        public void handle(OpenLiveMessageHandler handler, Long roomId, Danmu message) {
            JSONObject json = JSON.parseObject(message.getRaw());
            JSONArray info = json.getJSONArray("info");
            OpenLiveDanmakuMessage m = new OpenLiveDanmakuMessage();
            m.setDmType(info.getJSONArray(0).getInteger(1));
            m.setUid(info.getJSONArray(2).getLong(0));
            m.setUname(info.getJSONArray(2).getString(1));
            m.setRoomId(roomId);
            m.setMsg(info.getString(1));
            m.setTimestamp(info.getJSONArray(0).getLong(4));
            m.setSendTime(info.getJSONArray(0).getLong(4));
            handler.onDanmaku(m);
        }
    },
    PK_BATTLE_START("PK_BATTLE_START", "PK开始"),
    ENTRY_EFFECT("ENTRY_EFFECT", "进场特效"),
    NOTICE_MSG("NOTICE_MSG", "系统消息"),
    SUPER_CHAT_MESSAGE("SUPER_CHAT_MESSAGE", "醒目留言") {
        @Override
        public void handle(OpenLiveMessageHandler handler, Long roomId, Danmu message) {

            // 直接解析json字符串
            JSONObject json = JSON.parseObject(message.getRaw());
            JSONObject data = json.getJSONObject("data");
            JSONObject userInfo = data.getJSONObject("user_info");

            // 构建sc消息
            OpenLiveSuperChatMessage sc = new OpenLiveSuperChatMessage();
            sc.setRoomId(roomId);
            sc.setMessageId(json.getLong("msg_id"));
            sc.setUid(data.getLong("uid"));
            sc.setUname(userInfo.getString("uname"));
            sc.setUface(userInfo.getString("face"));
            sc.setMessage(data.getString("message"));
            sc.setStartTime(data.getLong("start_time"));
            sc.setEndTime(data.getLong("end_time"));
            sc.setPrice(data.getInteger("price"));
            sc.setTimestamp(data.getLong("time"));
            sc.setSendTime(data.getLong("time"));
            handler.onSuperChat(sc);
        }
    },
    SEND_GIFT("SEND_GIFT", "礼物") {
        @Override
        public void handle(OpenLiveMessageHandler handler, Long roomId, Danmu message) {
            // 直接解析json字符串
            JSONObject json = JSON.parseObject(message.getRaw());
            JSONObject data = json.getJSONObject("data");
            JSONObject senderInfo = data.getJSONObject("sender_uinfo");
            JSONObject sendBase = senderInfo.getJSONObject("base");
            JSONObject giftInfo = data.getJSONObject("gift_info");
            // 构建sc消息
            OpenLiveGiftMessage gift = new OpenLiveGiftMessage();
            gift.setRoomId(roomId);
            gift.setUid(senderInfo.getLong("uid"));
            gift.setUname(sendBase.getString("name"));
            gift.setGiftId(data.getInteger("giftId"));
            boolean hasImagedGift = giftInfo.getBooleanValue("has_imaged_gift");
            if (hasImagedGift) {
                gift.setGiftIcon(giftInfo.getString("gif"));
            } else {
                gift.setGiftIcon(giftInfo.getString("webp"));
            }
            handler.onGift(gift);
        }
    },
    USER_TOAST_MSG_V2("USER_TOAST_MSG_V2", "用户上舰") {
        @Override
        public void handle(OpenLiveMessageHandler handler, Long roomId, Danmu message) {
            JSONObject json = JSON.parseObject(message.getRaw());
            JSONObject data = json.getJSONObject("data");
            JSONObject userInfo = data.getJSONObject("sender_uinfo");
            JSONObject guardInfo = data.getJSONObject("guard_info");
            JSONObject userBaseInfo = userInfo.getJSONObject("base");

            OpenLiveGuardMessage msg = new OpenLiveGuardMessage();
            msg.setUid(userInfo.getLong("uid"));
            msg.setUname(userBaseInfo.getString("name"));
            msg.setUface(userBaseInfo.getString("face"));
            msg.setGuardLevel(guardInfo.getInteger("guard_level"));
            msg.setGuardLevelName(guardInfo.getString("role_name"));
            msg.setGuardType(guardInfo.getInteger("op_type"));
            Long startTime = guardInfo.getLong("start_time");
            Long endTime = guardInfo.getLong("end_time");
            msg.setGuardNum(getGuardNum(startTime, endTime));
            msg.setGuardUnit("月");
            msg.setRoomId(roomId);
            handler.onGuard(msg);
        }

        private int getGuardNum(Long startTime, Long endTime) {
            // 使用时间戳计算相差月份
            LocalDate date1 = LocalDate.ofEpochDay(startTime / (24 * 60 * 60 * 1000));
            LocalDate date2 = LocalDate.ofEpochDay(endTime / (24 * 60 * 60 * 1000));
            long months = ChronoUnit.MONTHS.between(date1, date2);
            return (int) months;
        }

    },
    ;

    private final String code;
    private final String desc;

    public boolean codeEquals(Object code) {
        return this.code.equals(code);
    }

    public static DanmuCmdEnums getByCode(Object code) {
        EnumSet<DanmuCmdEnums> enums = EnumSet.allOf(DanmuCmdEnums.class);
        return enums.stream()
                .filter(cmd -> cmd.codeEquals(code))
                .findFirst()
                .orElse(null);
    }

    public void handle(OpenLiveMessageHandler handler, Long roomId, Danmu message) {
    }
}
