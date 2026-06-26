package com.wallnet.bilibili.request;

import lombok.Data;

/**
 * @author skyli665
 * @date 2025-06-22 19:30
 */
@Data
public class Sender {
    private Integer senderId;
    private Integer receiverId;
    private String content;
}
