package com.wallnet.bilibili.response;

import cn.hutool.core.util.StrUtil;
import com.wallnet.bilibili.client.BiliClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author skyli665
 * @date 2026-01-22 17:41
 */
@Slf4j
@Getter
public class LiveTask {
    private String roomTitle;
    private String anchorName;
    private boolean isLive;
    private String streamUrl;
    private String quality;
    private Long roomId;
    private Long uid;
    @Setter
    private String fileName;

    public LiveTask(Long roomId) {
        this.roomId = roomId;
        this.getRoomInfo(roomId);
    }


    private boolean getRoomInfo(Long roomId) {
        this.roomId = roomId;
        try {
            // 首先获取房间初始化信息
            RoomInitInfo roomInitInfo = BiliClient.getRoomInitInfo(roomId);
            // 检查 realRoomId 和 uid 是否为 null
            if (roomId == null) {
                log.info("真实房间ID为空");
                return false;
            }

            uid = roomInitInfo.getUid();

            isLive = roomInitInfo.isLive();

            if (uid == null) {
                log.info("用户ID为空");
                return false;
            }

            // 获取房间详细信息
            RoomInfo roomInfo = BiliClient.getRoomInfo(roomId, roomTitle);
            if (roomInfo != null) {
                roomTitle = roomInfo.getTitle();
            }

            // 获取主播信息
            LiveUserInfo liveUserInfo = BiliClient.getLiveUserInfo(uid, roomTitle);

            if (liveUserInfo != null) {
                anchorName = liveUserInfo.getInfo().getUname();
            }

            if (!isLive) {
                // 不在直播，但状态获取成功
                log.info("房间当前不在直播");
                return true;
            }

            // 获取直播流信息
            RoomInitInfo liveStream = BiliClient.getLiveStream(1000, roomId, roomTitle);

            this.streamUrl = liveStream.getLiveStreamUrl();
            if (StrUtil.isBlank(streamUrl)) {
                log.info("无法获取直播流信息");
                return false;
            }
            this.quality = liveStream.getCurrentQn();
            return true;
        } catch (Exception e) {
            log.error("获取房间信息时出错: {}", e.getMessage());
            return false;
        }
    }
}
