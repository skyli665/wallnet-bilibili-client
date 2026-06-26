package com.wallnet.bilibili;

import com.wallnet.bilibili.client.LiveDanmuClient;
import com.wallnet.bilibili.client.LiveRecorder;
import com.wallnet.bilibili.response.LiveTask;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author skyli665
 * @date 2025-10-18 23:38
 */
@Slf4j
public class Test {

    public static void main(String[] args) {

        String cookieStr = "";
        // 获取二维码url 这里需要手动生成二维码
        // Object code = BiliClient.getQrCode();
        // log.info("===>二维码:{}", code);

        // 获取二维码状态 如果登录了，需要使用result来接收
        // LoginStatus loginResponse = BiliClient.loginStatus("");
        // log.info("===>登录状态:{}", loginResponse);

        // 获取用户信息
        // Object userInfo = BiliClient.getUserInfo(cookieStr);
        // log.info("===>用户信息:{}", userInfo);

        // 刷新token
        // LoginStatus refreshToken = BiliClient.getRefreshToken(cookieStr, "");

        // 获取用户动态
        // Reaction refreshToken = BiliClient.getReactionDetail(cookieStr, "{\"page\":514,\"like\":3546951740033480,\"repost\":1124767375522005014}", "1123917865400926215");
        // log.info("===>刷新token:{}", refreshToken);

        // 发送消息
        // Sender sender = new Sender();
        // sender.setContent("111");
        // sender.setReceiverId(123);
        // sender.setSenderId(123);
        // boolean sendPrivateMessage = BiliClient.sendPrivateMessage(cookieStr, sender);

        // 获取用户直播间信息
        // Object room = BiliClient.getRoom(5328L);

        // 获取用户直播间信息
        // LiveQuery query = new LiveQuery();
        // query.setUid(99L);
        // query.setRoomId(76L);
        // query.setPageIndex(1);
        // query.setPageSize(10);
        // Room userMonthVip = BiliClient.getUserMonthVip(cookieStr, query);

        // Sailors sailors = BiliClient.getSailors(cookieStr2, 1);
        // log.info("===>用户舰长:{}", sailors);

        // BiliClient.getLiveStream(1000, 96L, null);
        // log.info("===>用户直播间信息:{}", room);
        // BiliWbiInfo wbiInfo = BiliClient.getWbiInfo();
        // log.info("===>wbi信息:{}", wbiInfo);
        // 测试录播
        // testLive(30655190L);
        // 测试弹幕监听
        testDanmu(8792912L, cookieStr);
    }

    /**
     * 测试弹幕监听
     *
     * @param roomId
     * @param cookieStr
     */
    public static void testDanmu(Long roomId, String cookieStr) {
        LiveDanmuClient.Builder builder = new LiveDanmuClient.Builder();
        builder.roomId(roomId);
        builder.cookie(cookieStr);
        LiveDanmuClient liveDanmuClient = builder.build();
        liveDanmuClient.connectBlocking();
        while (true) {
            try {
                Thread.sleep(30000);
                if (liveDanmuClient.isOpen()) {
                    liveDanmuClient.sendHeartBeatPack();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void testLive(Long roomId) {
        // 获取任务信息
        log.info("=== B站直播录制器 ===");

        LiveTask liveTask = new LiveTask(roomId);
        // 获取房间信息
        if (liveTask.getRoomId() == null) {
            log.info("无法获取房间信息，录制终止");
            return;
        }

        log.info("房间标题: {}", liveTask.getRoomTitle());
        log.info("主播: {}", liveTask.getAnchorName());
        log.info("是否在直播: {}", liveTask.isLive());

        if (!liveTask.isLive()) {
            log.info("房间当前不在直播，录制终止");
            return;
        }

        log.info("直播流地址: {}", liveTask.getStreamUrl());
        log.info("清晰度: {}", liveTask.getQuality());

        // 生成录制文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String fileName = String.format("d://%s_%s_%s_%s.flv", liveTask.getAnchorName(), liveTask.getRoomId(), timestamp, liveTask.getQuality());
        liveTask.setFileName(fileName);
        LiveRecorder.startRecording(liveTask);
    }
}