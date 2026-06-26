package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author skyli665
 * @date 2026-01-24 22:36
 */
@Data
public class LiveDanmuInfo {
    
    private String group;
    @JSONField(name = "business_id")
    private Long businessId;
    @JSONField(name = "refresh_row_factor")
    private Double refreshRowFactor;
    @JSONField(name = "max_delay")
    private Long maxDelay;
    private String token;
    @JSONField(name = "host_list")
    private List<HostInfo> hostList;
    
    
    @Data
    public static class HostInfo {
        @JSONField(name = "host")
        private String host;
        @JSONField(name = "port")
        private Integer port;
        @JSONField(name = "wss_port")
        private Integer wssPort;
        @JSONField(name = "ws_port")
        private Integer wsPort;
        
        public String getWsUrl() {
            return "ws://" + host + ":" + wsPort + "/sub";
        }
        
        public String getWssUrl() {
            return "wss://" + host + ":" + wssPort + "/sub";
        }
        
    }
    
    
}
