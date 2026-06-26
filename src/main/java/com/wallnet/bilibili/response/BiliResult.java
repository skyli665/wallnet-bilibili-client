package com.wallnet.bilibili.response;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;

/**
 * @author skyli665
 * @date 2025-06-22 16:33
 */
@Data
public class BiliResult {
    private static final String SUCCESS = "请求成功";
    private static final int SUCCESS_CODE = 200;
    
    private Integer code;
    private String msg;
    private String data;
    private List<String> cookie;
    
    
    @SneakyThrows
    public static BiliResult success(Object data) {
        BiliResult result = new BiliResult();
        result.setCode(SUCCESS_CODE);
        if (null != data) {
            // 返回json串
            result.setData(JSON.toJSONString(data));
        }
        result.setMsg(SUCCESS);
        return result;
    }
    
    public static BiliResult success() {
        return success(null);
    }
    
    public static BiliResult fail(Integer code, String msg) {
        BiliResult result = new BiliResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    
    public boolean isSuccess() {
        return 0 == code;
    }
    
    @SneakyThrows
    public static BiliResult create(String response) {
        return JSON.parseObject(response, BiliResult.class);
    }
    
    @SneakyThrows
    public <T> Object getData(Class<T> clazz) {
        return JSON.parseObject(data, clazz);
    }
}
