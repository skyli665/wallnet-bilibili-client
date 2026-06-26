package com.wallnet.bilibili.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DanmuCmdEnums {

    TALK_MSG("DANMU_MSG", "弹幕消息"),
    PK_BATTLE_START("PK_BATTLE_START", "PK开始"),
    ENTRY_EFFECT("ENTRY_EFFECT", "进场特效"),
    NOTICE_MSG("NOTICE_MSG", "系统消息"),
    SUPER_CHAT_MESSAGE("SUPER_CHAT_MESSAGE", "醒目留言"),
    SEND_GIFT("SEND_GIFT", "礼物"),
    USER_TOAST_MSG_V2("USER_TOAST_MSG_V2", "用户上舰"),
    ;

    private final String code;
    private final String desc;

    public boolean codeEquals(Object code) {
        return this.code.equals(code);
    }
}
