package com.wallnet.bilibili.handler;

import com.alibaba.fastjson2.JSON;
import com.wallnet.bilibili.handler.impl.GiftHandler;
import com.wallnet.bilibili.handler.impl.MessageHandler;
import com.wallnet.bilibili.handler.impl.ScHandler;
import com.wallnet.bilibili.response.Danmu;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author skyli665
 * @date 2026-06-26 14:52
 */
@Slf4j
public final class HandlerFactory {

    private static final List<Handler> handlers = new ArrayList<>();

    static {
        handlers.add(new ScHandler());
        handlers.add(new MessageHandler());
        handlers.add(new GiftHandler());
    }


    public static void addHandler(Handler... args) {
        handlers.addAll(Arrays.asList(args));
    }

    public static void handle(Long roomId, String message) {
        log.debug("===>收到{}房间弹幕:{}", roomId, message);
        for (Handler handler : handlers) {
            Danmu parse = JSON.parseObject(message, Danmu.class);
            if (parse == null) {
                break;
            }
            parse.setRaw(message);
            if (handler.getType().equals(parse.getCmd())) {
                handler.handle(roomId, parse);
            }
        }
    }
}
