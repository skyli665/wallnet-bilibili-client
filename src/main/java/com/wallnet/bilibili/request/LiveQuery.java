package com.wallnet.bilibili.request;

import lombok.Data;

/**
 * @author skyli665
 * @date 2025-06-22 20:02
 */
@Data
public class LiveQuery extends PageQuery {
    private Long roomId;
    private Long uid;
}
