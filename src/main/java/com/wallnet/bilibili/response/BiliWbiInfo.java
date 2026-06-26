package com.wallnet.bilibili.response;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author skyli665
 * @date 2026-01-24 22:09
 */
@Data
public class BiliWbiInfo {
    
    @JSONField(name = "wbi_img")
    private WbiImg wbiImg;
    
    public String getImgKey() {
        return getKey(wbiImg.getImgUrl());
    }
    
    public String getSubKey() {
        return getKey(wbiImg.getSubUrl());
    }
    
    private String getKey(String key) {
        if (StrUtil.isNotBlank(key)) {
            return key.substring(key.lastIndexOf("/") + 1, key.lastIndexOf("."));
        }
        return "";
    }
    
    @Data
    public static class WbiImg {
        @JSONField(name = "img_url")
        private String imgUrl;
        @JSONField(name = "sub_url")
        private String subUrl;
    }
}
