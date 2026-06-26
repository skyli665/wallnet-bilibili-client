package com.wallnet.bilibili.response;

import lombok.Data;

import java.util.List;

/**
 * @author skyli665
 * @date 2025-06-22 21:55
 */
@Data
public class Room {
    private List<RoomUser> list;
    private RoomInfo info = new RoomInfo();
    private String title;
    private String url;
    private Integer roomid;
    private String cover;
    private Integer online;
    private Integer liveStatus;
    
    
    public int getPageIndex() {
        return info.getNow();
    }
    
    public int getCount() {
        return info.getNum();
    }
    
    public boolean isOnline() {
        return liveStatus == 1;
    }
    
    @Data
    public static class RoomInfo {
        /**
         * 直播间人数
         */
        private Integer num = 0;
        /**
         * 当前页面
         */
        private Integer now = 1;
        /**
         * 总页数
         */
        private Integer page = 1;
    }
}
