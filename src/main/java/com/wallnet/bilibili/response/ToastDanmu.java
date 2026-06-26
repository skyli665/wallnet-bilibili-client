package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author skyli665
 * @date 2026-06-26 12:04
 */
@Data
public class ToastDanmu extends Danmu {

    @JSONField(name = "data")
    private ToastData data;

    @Data
    public class ToastData {
        // 舰长信息
        @JSONField(name = "guard_info")
        private GuardInfo guardInfo;
        // 支付信息
        @JSONField(name = "pay_info")
        private PayInfo payInfo;
        // 发送者信息
        @JSONField(name = "sender_uinfo")
        private SenderUinfo senderUinfo;
        // 提示信息
        @JSONField(name = "toast_msg")
        private String toastMsg;
    }

    // 发送者信息
    @Data
    public class SenderUinfo {
        private Long uid;
        private SenderUinfoBase base;
    }

    // 发送者基础信息
    @Data
    public class SenderUinfoBase {
        private String face;
        private String name;
    }

    // 支付信息
    @Data
    public class PayInfo {
        private Integer num;
        // 支付流水号
        @JSONField(name = "payflow_id")
        private String payflowId;
        private Integer price;
        private String unit;
    }

    @Data
    public class GuardInfo {
        // 舰长结束时间
        @JSONField(name = "end_time")
        private Integer endTime;
        // 舰长等级
        @JSONField(name = "guard_level")
        private Integer guardLevel;
        // 操作类型
        @JSONField(name = "op_type")
        private Integer opType;
        // 舰长角色名称
        @JSONField(name = "role_name")
        private String roleName;
        // 房间内舰长数量
        @JSONField(name = "room_guard_count")
        private Integer roomGuardCount;
        // 舰长开始时间
        @JSONField(name = "start_time")
        private Integer startTime;
    }

    @Data
    public class GiftInfo {
        // 礼物ID
        @JSONField(name = "gift_id")
        private Integer giftId;
    }
}
