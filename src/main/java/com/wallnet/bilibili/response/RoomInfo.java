package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author skyli665
 * @date 2026-01-20 23:34
 */
@Data
public class RoomInfo {
    
    private Long uid;
    @JSONField(name = "room_id")
    private Long roomId;
    @JSONField(name = "short_id")
    private Long shortId;
    private Integer attention;
    private Integer online;
    @JSONField(name = "is_portrait")
    private Boolean isPortrait;
    private String description;
    @JSONField(name = "live_status")
    private String liveStatus;
    @JSONField(name = "area_id")
    private Integer areaId;
    @JSONField(name = "parent_area_id")
    private Integer parentAreaId;
    @JSONField(name = "parent_area_name")
    private String parentAreaName;
    @JSONField(name = "old_area_id")
    private Integer oldAreaId;
    private String background;
    private String title;
    @JSONField(name = "user_cover")
    private String userCover;
    private String keyframe;
    @JSONField(name = "is_strict_room")
    private Boolean isStrictRoom;
    @JSONField(name = "live_time")
    private LocalDateTime liveTime;
    private String tags;
    @JSONField(name = "is_anchor")
    private String isAnchor;
    @JSONField(name = "room_silent_type")
    private String roomSilentType;
    @JSONField(name = "room_silent_level")
    private Integer roomSilentLevel;
    @JSONField(name = "room_silent_second")
    private Integer roomSilentSecond;
    @JSONField(name = "area_name")
    private String areaName;
    private String pendants;
    @JSONField(name = "area_pendants")
    private String areaPendants;
    @JSONField(name = "hot_words")
    private List<String> hotWords;
    @JSONField(name = "hot_words_status")
    private Integer hotWordsStatus;
    private String verify;
    @JSONField(name = "up_session")
    private String upSession;
    @JSONField(name = "pk_status")
    private Integer pkStatus;
    @JSONField(name = "pk_id")
    private Integer pkId;
    @JSONField(name = "battle_id")
    private Integer battleId;
    @JSONField(name = "allow_change_area_time")
    private Integer allowChangeAreaTime;
    @JSONField(name = "allow_upload_cover_time")
    private Integer allowUploadCoverTime;
    private NewPendants newPendants;
    
    @Data
    public class NewPendants {
        private Frame frame;
        private String badge;
        @JSONField(name = "mobile_frame")
        private Frame mobileFrame;
        @JSONField(name = "mobile_badge")
        private String mobileBadge;
    }
    
    @Data
    public class Frame {
        private String name;
        private String value;
        private Integer position;
        private String desc;
        private Integer area;
        @JSONField(name = "area_old")
        private Integer areaOld;
        @JSONField(name = "bg_color")
        private String bgColor;
        @JSONField(name = "bg_pic")
        private String bgPic;
        @JSONField(name = "use_old_area")
        private Boolean useOldArea;
    }
    
    @Data
    public class StudioInfo {
        private Integer status;
        @JSONField(name = "master_list")
        private List<String> masterList;
    }
}
