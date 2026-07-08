package com.wallnet.bilibili.client;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wallnet.bilibili.common.BiliConst;
import com.wallnet.bilibili.handler.MessageQueueExecutor;
import com.wallnet.bilibili.response.Danmu;
import com.wallnet.bilibili.response.WebSocketInfo;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 开放平台WebSocket客户端
 * 参考blivechat项目(open_live.py)移植实现
 */
@Slf4j
public class OpenLiveWsClient extends WebSocketClient {

    private static final int DEFAULT_RECONNECT_INTERVAL = 1;
    private static final int MAX_RECONNECT_INTERVAL = 60;

    private final String idCode;
    private final Long appId;
    private final String key;
    private final String secret;
    private List<String> wssLinks;
    private String gameId;
    @Getter
    private Long roomOwnerUid;
    @Getter
    private Long roomId;
    private MessageQueueExecutor messageQueueExecutor;
    private ScheduledExecutorService scheduler;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger reconnectCount = new AtomicInteger(0);

    private ScheduledFuture<?> heartbeatFuture;
    private ScheduledFuture<?> appHeartbeatFuture;

    private OpenLiveWsClient(URI uri, String idCode, Long appId, String key, String secret, List<String> wssLinks) {
        super(uri);
        this.idCode = idCode;
        this.appId = appId;
        this.key = key;
        this.secret = secret;
        this.wssLinks = wssLinks;
        setConnectionLostTimeout(0);
    }

    @Override
    @SneakyThrows
    public boolean connectBlocking() {
        boolean connected = super.connectBlocking();
        if (!connected) {
            log.error("连接到WebSocket服务器失败: {}", uri.toString());
        } else {
            log.info("WebSocket连接已建立，应用ID：{}", appId);
            isRunning.set(true);
        }
        return connected;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.debug("WebSocket连接已打开");
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = JSON.parseObject(message);
            String cmd = json.getString("cmd");
            if (cmd == null) {
                return;
            }
            int colonIndex = cmd.indexOf(':');
            if (colonIndex != -1) {
                cmd = cmd.substring(0, colonIndex);
            }
            String data = json.getString("data");
            if (data == null) {
                return;
            }
            if (messageQueueExecutor == null) {
                return;
            }
            Danmu danmu = new Danmu();
            danmu.setCmd(cmd);
            danmu.setRaw(data);
            danmu.setRoomId(roomId);
            messageQueueExecutor.addDanmu(danmu);
        } catch (Exception e) {
            log.error("解析消息失败: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("WebSocket连接已关闭，原因：{}，code：{}", reason, code);
        isRunning.set(false);
        stopHeartBeat();
        handleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket连接发生错误", ex);
    }

    @Override
    @SneakyThrows
    public void onMessage(ByteBuffer bytes) {
        while (bytes.hasRemaining()) {
            int packageLen = bytes.getInt();
            short headLength = bytes.getShort();
            short ver = bytes.getShort();
            int op = bytes.getInt();
            int seq = bytes.getInt();

            log.debug("收到WebSocket消息: packageLen={}, headLength={}, ver={}, op={}, seq={}",
                    packageLen, headLength, ver, op, seq);

            byte[] contentBytes = new byte[packageLen - headLength];
            bytes.get(contentBytes);

            if (op == BiliConst.WSOpt.AUTH_REPLY) {
                log.debug("收到鉴权回复");
                handleAuthReply(contentBytes);
            } else if (op == BiliConst.WSOpt.SEND_SMS_REPLY) {
                log.debug("收到业务消息, 内容: {}", new String(contentBytes, StandardCharsets.UTF_8));
                handleSmsReply(contentBytes);
            } else if (op == BiliConst.WSOpt.HEARTBEAT_REPLY) {
                log.debug("收到心跳回复");
                handleHeartbeatReply(contentBytes);
            } else {
                log.warn("收到未知操作码的消息: op={}, content={}", op, new String(contentBytes, StandardCharsets.UTF_8));
            }
        }
    }

    private void handleAuthReply(byte[] contentBytes) {
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        JSONObject respBody = JSON.parseObject(content);
        if (respBody.getIntValue("code") == 0) {
            log.info("鉴权成功");
            startHeartBeat();
        } else {
            log.error("鉴权失败: {}", content);
            close();
        }
    }

    private void handleSmsReply(byte[] contentBytes) {
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        onMessage(content);
    }

    private void handleHeartbeatReply(byte[] contentBytes) {
        if (contentBytes.length >= 4) {
            int popularity = ByteBuffer.wrap(contentBytes).getInt();
            log.debug("收到心跳回复，人气值：{}", popularity);
        }
    }

    @SneakyThrows
    public void sendAuth(String authBody) {
        log.info("发送鉴权包, authBody: {}", authBody);
        byte[] authPacket = pack(authBody, BiliConst.WSOpt.AUTH);
        log.debug("鉴权包内容: {}", bytesToHex(authPacket));
        send(authPacket);
        log.debug("已发送鉴权包");
    }

    @SneakyThrows
    public void sendHeartBeat() {
        if (isClosed()) {
            log.warn("WebSocket已关闭，跳过心跳");
            return;
        }
        send(pack("", BiliConst.WSOpt.HEARTBEAT));
        log.debug("已发送心跳包");
    }

    @SneakyThrows
    public static byte[] pack(String body, int op) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        try (ByteArrayOutputStream data = new ByteArrayOutputStream();
             DataOutputStream stream = new DataOutputStream(data)) {
            stream.writeInt(bodyBytes.length + 16);
            stream.writeShort(16);
            stream.writeShort(BiliConst.Version.NORMAL);
            stream.writeInt(op);
            stream.writeInt(1);
            stream.write(bodyBytes);
            return data.toByteArray();
        }
    }

    public void sendAppHeartBeat() {
        if (StrUtil.isBlank(gameId)) {
            log.warn("游戏ID为空，跳过应用心跳");
            return;
        }
        if (!BiliClient.openLiveHeartBeat(gameId, key, secret)) {
            log.error("应用心跳失败");
        }
    }

    public void endApp() {
        if (StrUtil.isBlank(gameId)) {
            log.warn("游戏ID为空，跳过关闭应用");
            return;
        }
        if (!BiliClient.endApp(gameId, appId, key, secret)) {
            log.error("关闭应用失败");
        }
    }

    private void startHeartBeat() {
        stopHeartBeat();
        heartbeatFuture = scheduler.scheduleAtFixedRate(this::sendHeartBeat, 0, BiliConst.WSOpt.DEFAULT_HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
        appHeartbeatFuture = scheduler.scheduleAtFixedRate(this::sendAppHeartBeat, 0, BiliConst.WSOpt.DEFAULT_HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
        log.info("心跳任务已启动");
    }

    private void stopHeartBeat() {
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(false);
            heartbeatFuture = null;
        }
        if (appHeartbeatFuture != null) {
            appHeartbeatFuture.cancel(false);
            appHeartbeatFuture = null;
        }
        log.info("心跳任务已停止");
    }

    private void handleReconnect() {
        if (!isRunning.get()) {
            int count = reconnectCount.incrementAndGet();
            int interval = Math.min(DEFAULT_RECONNECT_INTERVAL * count, MAX_RECONNECT_INTERVAL);
            log.info("准备重连，尝试次数：{}，间隔：{}秒", count, interval);

            scheduler.schedule(() -> {
                try {
                    List<String> wssLinks = this.wssLinks;
                    int reinitPeriod = Math.max(3, wssLinks != null ? wssLinks.size() : 1);

                    if (wssLinks == null || (count > 0 && count % reinitPeriod == 0)) {
                        WebSocketInfo info = BiliClient.getWebsocketInfo(idCode, appId, key, secret);
                        log.info("重新获取WebSocket信息, gameId: {}", info.getGameId());
                        wssLinks = info.getWssLinks();
                        this.wssLinks = wssLinks;
                        this.gameId = info.getGameId();
                    }

                    int urlIndex = (count - 1) % wssLinks.size();
                    String wssUrl = wssLinks.get(urlIndex);
                    log.info("使用WebSocket地址[{}/{}]: {}", urlIndex + 1, wssLinks.size(), wssUrl);

                    URI uri = URI.create(wssUrl);
                    OpenLiveWsClient newClient = new OpenLiveWsClient(uri, idCode, appId, key, secret, wssLinks);
                    newClient.messageQueueExecutor = this.messageQueueExecutor;
                    newClient.gameId = this.gameId;

                    if (newClient.connectBlocking()) {
                        newClient.sendAuth(BiliClient.getWebsocketInfo(idCode, appId, key, secret).getAuthBody());
                        log.info("重连成功");
                        isRunning.set(true);
                        reconnectCount.set(0);
                    } else {
                        log.error("重连失败");
                        handleReconnect();
                    }
                } catch (Exception e) {
                    log.error("重连异常", e);
                    handleReconnect();
                }
            }, interval, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        isRunning.set(false);
        stopHeartBeat();
        endApp();
        close();
    }

    public static final class Builder {
        private String idCode;
        private Long appId;
        private String key;
        private String secret;
        private MessageQueueExecutor messageQueueExecutor;
        private ScheduledExecutorService scheduler;

        public Builder idCode(String idCode) {
            this.idCode = idCode;
            return this;
        }

        public Builder appId(Long appId) {
            this.appId = appId;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Builder messageQueueExecutor(MessageQueueExecutor messageQueueExecutor) {
            this.messageQueueExecutor = messageQueueExecutor;
            return this;
        }

        public Builder scheduler(ScheduledExecutorService scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public OpenLiveWsClient build() {
            WebSocketInfo info = BiliClient.getWebsocketInfo(idCode, appId, key, secret);
            List<String> wssLinks = info.getWssLinks();
            URI uri = URI.create(wssLinks.get(0));
            OpenLiveWsClient client = new OpenLiveWsClient(uri, idCode, appId, key, secret, wssLinks);
            client.gameId = info.getGameId();
            client.roomId = info.getRoomId();
            client.roomOwnerUid = info.getUid();
            client.messageQueueExecutor = this.messageQueueExecutor;
            client.connectBlocking();
            client.sendAuth(info.getAuthBody());
            client.scheduler = this.scheduler;
            return client;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}