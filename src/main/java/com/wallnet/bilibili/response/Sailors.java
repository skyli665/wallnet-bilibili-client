package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author skyli665
 * @date 2025-10-26 03:07
 */
@Data
public class Sailors {
    
    private List<Sailor> list;
    @JSONField(name = "guard_warn")
    private GuardWarn guardWarn;
    private PageInfo pageInfo;
    
    
    /**
     * 船员信息
     */
    @Data
    public class Sailor {
        private Long uid;
        private String username;
        @JSONField(name = "guard_level")
        private Integer guardLevel;
        @JSONField(name = "guard_level_name")
        private String guardLevelName;
        private BigDecimal score;
        @JSONField(name = "expired_time")
        private LocalDate expiredTime;
        
    }
    
    @Data
    public class GuardWarn {
        @JSONField(name = "is_warn")
        private Boolean isWarn;
        private String warn;
        private Long expired;
        @JSONField(name = "will_expired")
        private Long willExpired;
        private String address;
    }
}
