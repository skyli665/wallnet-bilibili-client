package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author skyli665
 * @date 2025-06-22 17:42
 */
@Data
public class LoginStatus {
    private String url;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    private Integer code;
    private String status;
    private String message;
    private String cookies;
    private Long timestamp;
    
    public boolean isSuccess() {
        return 0 == code;
    }
}
