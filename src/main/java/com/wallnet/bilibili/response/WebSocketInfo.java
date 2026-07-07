package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * WebSocket连接信息
 */
@Data
@AllArgsConstructor
public class WebSocketInfo {
    @JSONField(name = "anchor_info")
    private AnchorInfo anchorInfo;
    @JSONField(name = "game_info")
    private GameInfo gameInfo;
    @JSONField(name = "websocket_info")
    private WsInfo wsInfo;

    @Data
    public class WsInfo {
        @JSONField(name = "auth_body")
        private String authBody;
        @JSONField(name = "wss_link")
        private List<String> wssLink;
    }

    @Data
    public class GameInfo {
        @JSONField(name = "game_id")
        private String gameId;
    }

    @Data
    public class AnchorInfo {
        @JSONField(name = "open_id")
        private String openId;
        @JSONField(name = "room_id")
        private String roomId;
        private String uface;
        private String uname;
        private Long uid;
        @JSONField(name = "union_id")
        private String unionId;
    }

    public String getAuthBody() {
        return wsInfo.getAuthBody();
    }

    public List<String> getWssLinks() {
        return wsInfo.getWssLink();
    }

    public String getGameId() {
        return gameInfo.getGameId();
    }

    public String getOpenId() {
        return anchorInfo.getOpenId();
    }

    public String getRoomId() {
        return anchorInfo.getRoomId();
    }

    public String getUface() {
        return anchorInfo.getUface();
    }

    public String getUname() {
        return anchorInfo.getUname();
    }

    public Long getUid() {
        return anchorInfo.getUid();
    }

    public String getUnionId() {
        return anchorInfo.getUnionId();
    }
}

