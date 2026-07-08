package com.wallnet.bilibili;

import com.wallnet.bilibili.client.LiveDanmuClient;
import com.wallnet.bilibili.client.LiveRecorder;
import com.wallnet.bilibili.client.OpenLiveWsClient;
import com.wallnet.bilibili.handler.MessageQueueExecutor;
import com.wallnet.bilibili.handler.OpenLiveMessageHandler;
import com.wallnet.bilibili.response.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author skyli665
 * @date 2025-10-18 23:38
 */
@Slf4j
public class Test {

    public static void main(String[] args) {

        String cookieStr = "";
        // 测试直播ws
        // testLiveWs();
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
//        LoginStatus refreshToken = BiliClient.getRefreshToken(cookieStr, "");
//        log.info("===>刷新token:{}", refreshToken);

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
        // Object room = BiliClient.getRoom(3537123466545328L);

        // 获取用户直播间信息
        // LiveQuery query = new LiveQuery();
        // query.setUid(471688299L);
        // query.setRoomId(21592776L);
        // query.setPageIndex(1);
        // query.setPageSize(10);
        // Room userMonthVip = BiliClient.getUserMonthVip(cookieStr, query);

        // Sailors sailors = BiliClient.getSailors(cookieStr2, 1);
        // log.info("===>用户舰长:{}", sailors);

        // BiliClient.getLiveStream(1000, 7170396L, null);
        // log.info("===>用户直播间信息:{}", room);
        // BiliWbiInfo wbiInfo = BiliClient.getWbiInfo();
        // log.info("===>wbi信息:{}", wbiInfo);
        // 测试录播
        // testLive(30655190L);
        // 测试弹幕监听
        testDanmu(22632424L, cookieStr);
    }

    /**
     * 测试弹幕监听
     *
     * @param roomId
     * @param cookieStr
     */
    public static void testDanmu(Long roomId, String cookieStr) {
        OpenLiveMessageHandler handler = new OpenLiveMessageHandler() {
            @Override
            public void onDanmaku(OpenLiveDanmakuMessage message) {
                log.info("[弹幕] {}: {}", message.getUname(), message.getMsg());
            }

            @Override
            public void onGift(OpenLiveGiftMessage message) {
                log.info("[礼物] {} 赠送 {}x{}", message.getUname(), message.getGiftName(), message.getGiftNum());
            }

            @Override
            public void onGuard(OpenLiveGuardMessage message) {
                log.info("[舰长] {} 开通了舰长", message.getUname());
            }

            @Override
            public void onSuperChat(OpenLiveSuperChatMessage message) {
                log.info("[醒目留言] {}: {}", message.getUname(), message.getMessage());
            }

            @Override
            public void onSuperChatDelete(Long messageId) {
                log.info("[删除醒目留言] messageId: {}", messageId);
            }

            @Override
            public void onLike(OpenLiveLikeMessage message) {
                log.info("[点赞] {} 点赞了", message.getUname());
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "live-ws-heartbeat");
            t.setDaemon(true);
            return t;
        });

        // 单例常量
        MessageQueueExecutor INSTANCE = new MessageQueueExecutor(handler);

        LiveDanmuClient.Builder builder = new LiveDanmuClient.Builder();
        builder.roomId(roomId);
        builder.cookie(cookieStr);
        builder.messageQueueExecutor(INSTANCE);
        builder.scheduler(scheduler);
        LiveDanmuClient liveDanmuClient = builder.build();
        liveDanmuClient.connectBlocking();
        INSTANCE.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("正在关闭 LiveDanmuClient...");
            scheduler.shutdownNow();
            INSTANCE.stop();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    INSTANCE.stop();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                INSTANCE.stop();
                Thread.currentThread().interrupt();
            }
        }));


        while (liveDanmuClient.isOpen()) {
            try {
                Thread.sleep(10000);
                log.info("运行中...");
            } catch (InterruptedException e) {
                break;
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

    /**
     * 测试弹幕监听
     *
     */
    public static void testLiveWs() {
        String idCode = "";
        Long appId = 0L;
        String accessKeyId = "";
        String accessKeySecret = "";


        OpenLiveMessageHandler handler = new OpenLiveMessageHandler() {
            @Override
            public void onDanmaku(OpenLiveDanmakuMessage message) {
                Integer dmType = message.getDmType();
                if (dmType == 1) {
                    log.info("[表情] {}: {}", message.getUname(), message.getEmojiImgUrl());
                } else {
                    log.info("[弹幕] {}: {}", message.getUname(), message.getMsg());
                }
            }

            @Override
            public void onGift(OpenLiveGiftMessage message) {
                log.info("[礼物] {} 赠送 {}x{} ￥{}", message.getUname(), message.getGiftName(), message.getGiftNum(), message.getPrice());
            }

            @Override
            public void onGuard(OpenLiveGuardMessage message) {
                log.info("[舰长] {} 开通了舰长", message.getUname());
            }

            @Override
            public void onSuperChat(OpenLiveSuperChatMessage message) {
                log.info("[醒目留言] {}: {}", message.getUname(), message.getMessage());
            }

            @Override
            public void onSuperChatDelete(Long messageId) {
                log.info("[删除醒目留言] messageId: {}", messageId);
            }

            @Override
            public void onLike(OpenLiveLikeMessage message) {
                log.info("[点赞] {} 点赞了", message.getUname());
            }

            @Override
            public void onLiveEnd(Danmu data) {
                log.info("[直播结束] roomId: {}, gameId: {}", data.getRoomId(), data.getGameId());
            }

            @Override
            public void onRoomEnter(OpenLiveRoomEnterMessage message) {
                log.info("[用户进入直播间] {}: {}", message.getUname(), message.getOpenId());
            }

            @Override
            public void onLiveStart(OpenLiveStartMessage message) {
                log.info("[直播开始] roomId: {}, areaId: {}, title: {}", message.getRoomId(), message.getAreaId(), message.getTitle());
            }

        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "live-ws-heartbeat");
            t.setDaemon(true);
            return t;
        });

        // 单例常量
        MessageQueueExecutor INSTANCE = new MessageQueueExecutor(handler);

        OpenLiveWsClient client = new OpenLiveWsClient.Builder()
                .idCode(idCode)
                .appId(appId)
                .key(accessKeyId)
                .secret(accessKeySecret)
                .messageQueueExecutor(INSTANCE)
                .scheduler(scheduler)
                .build();

        log.info("LiveWsClient 已启动，房间ID: {}, 房间主UID: {}", client.getRoomId(), client.getRoomOwnerUid());

        INSTANCE.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("正在关闭 LiveWsClient...");
            client.stop();
            scheduler.shutdownNow();
            INSTANCE.stop();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    INSTANCE.stop();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                INSTANCE.stop();
                Thread.currentThread().interrupt();
            }
        }));

        while (client.isOpen()) {
            try {
                Thread.sleep(10000);
                log.info("运行中...");
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}