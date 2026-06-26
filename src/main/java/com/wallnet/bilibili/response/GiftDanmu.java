package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author skyli665
 * @date 2026-06-26 11:40
 */
@Data
public class GiftDanmu extends Danmu {

    private GiftData data;

    @Data
    public class GiftData {
        private String action;
        private Long giftId;
        private String giftName;
        private Integer giftType;
        // 礼物信息
        @JSONField(name = "gift_info")
        private GiftInfo giftInfo;
        private Integer num;
        private Integer price;
        // 接收用户信息
        @JSONField(name = "receive_user_info")
        private ReceiveUserInfo receiveUserInfo;
        // 发送用户信息
        @JSONField(name = "sender_uinfo")
        private SenderUserInfo senderUinfo;
        @JSONField(name = "wealth_level")
        private Integer wealthLevel;
        private Long uid;
        private String uname;
    }

    @Data
    public class GiftInfo {
        @JSONField(name = "effect_id")
        private Integer effectId;
        private String gif;
        @JSONField(name = "has_imaged_gift")
        private Integer hasImagedGift;
        @JSONField(name = "img_basic")
        private String imgBasic;
        private String webp;
    }

    @Data
    public class ReceiveUserInfo {
        private Long uid;
        private String uname;
    }

    @Data
    public class SenderUserInfo {
        private SenderUserInfoBase base;
        private String title;
        private Long uid;
        private String wealth;
    }

    @Data
    public class SenderUserInfoBase {
        private String face;
        @JSONField(name = "is_mystery")
        private Boolean isMystery;
        private String name;
    }

    public String print() {
        // 用户送礼物给用户
        return String.format("%s送了%s个%s给%s,价格:%s", data.uname, data.num, data.giftName, data.receiveUserInfo.getUname(), data.price);
    }


}
