package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OpenLiveGiftMessage extends Danmu {
    private Long roomId;
    private Long uid;
    private String uname;
    private String uface;

    @JSONField(name = "gift_id")
    private Integer giftId;

    @JSONField(name = "gift_name")
    private String giftName;

    @JSONField(name = "gift_num")
    private Integer giftNum;

    private Integer price;
    private Boolean paid;

    @JSONField(name = "fans_medal_level")
    private Integer fansMedalLevel;

    @JSONField(name = "fans_medal_name")
    private String fansMedalName;

    @JSONField(name = "fans_medal_wearing_status")
    private Boolean fansMedalWearingStatus;

    @JSONField(name = "guard_level")
    private Integer guardLevel;

    private Long timestamp;

    @JSONField(name = "anchor_info")
    private AnchorInfo anchorInfo;

    @JSONField(name = "msg_id")
    private String msgId;

    @JSONField(name = "gift_icon")
    private String giftIcon;

    @JSONField(name = "combo_gift")
    private Boolean comboGift;

    @JSONField(name = "combo_info")
    private ComboInfo comboInfo;

    @Data
    public static class AnchorInfo {
        private Long uid;
        private String uname;
        private String uface;
    }

    @Data
    public static class ComboInfo {
        @JSONField(name = "combo_base_num")
        private Integer comboBaseNum;

        @JSONField(name = "combo_count")
        private Integer comboCount;

        @JSONField(name = "combo_id")
        private String comboId;

        @JSONField(name = "combo_timeout")
        private Integer comboTimeout;
    }
}