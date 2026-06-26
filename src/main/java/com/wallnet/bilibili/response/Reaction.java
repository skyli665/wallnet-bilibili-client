package com.wallnet.bilibili.response;

import lombok.Data;

import java.util.List;

/**
 * @author skyli665
 * @date 2025-10-19 03:00
 */
@Data
public class Reaction {
    
    
    private boolean hasMore;
    private List<ReactionItem> items;
    private String offset;
    private String total;
    
    
    @Data
    public static final class ReactionItem {
        private String action;
        private Integer attend;
        private Long mid;
        private String name;
        
        /**
         * 是否是点赞
         *
         * @return
         */
        public boolean isGood() {
            return "赞了".equals(action);
        }
        
        /**
         * 是否是转发
         *
         * @return
         */
        public boolean isForward() {
            return "转发了".equals(action);
        }
        
        /**
         * 是否关注
         */
        public boolean isAttend() {
            return attend > 0;
        }
    }
}
