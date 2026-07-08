package com.wallnet.bilibili.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wallnet.bilibili.common.BiliConst;
import com.wallnet.bilibili.handler.MessageQueueExecutor;
import com.wallnet.bilibili.response.BiUserInfo;
import com.wallnet.bilibili.response.Danmu;
import com.wallnet.bilibili.response.LiveDanmuInfo;
import com.wallnet.bilibili.utils.BrotliUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.wallnet.bilibili.common.BiliConst.USER_AGENT;

/**
 * @author skyli665
 * @date 2026-06-26 14:39
 */
@Slf4j
public class LiveDanmuClient extends WebSocketClient {

    private final Long roomId;
    private final String token;
    private final Long uid;
    private MessageQueueExecutor messageQueueExecutor;
    private ScheduledExecutorService scheduler;

    private ScheduledFuture<?> heartbeatFuture;

    private LiveDanmuClient(URI uri, Long roomId, String cookie, Long uid, String token) {
        super(uri);
        this.roomId = roomId;
        this.token = token;
        this.uid = uid;
        addHeader("Referer", "https://live.bilibili.com/");
        addHeader("User-Agent", USER_AGENT);
        addHeader("Accept", "*/*");
        addHeader("Connection", "keep-alive");
        // 禁用内置的连接丢失检测（使用自定义心跳机制）
        setConnectionLostTimeout(0);
        if (StrUtil.isNotBlank(cookie)) {
            addHeader("Cookie", cookie);
        }
    }

    @Override
    @SneakyThrows
    public boolean connectBlocking() {
        boolean connected = super.connectBlocking();
        if (!connected) {
            log.error("连接到弹幕服务器失败: {}", uri.toString());
        } else {
            // 发送认证包
            send(generateAuthPack());
            log.info("弹幕监听器已启动，房间号：{}", roomId);
        }
        return connected;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.debug("WebSocket连接已打开，房间号：{}", roomId);
    }

    @Override
    public void onMessage(String message) {
        Danmu parse = JSON.parseObject(message, Danmu.class);
        if (parse != null && messageQueueExecutor != null) {
            parse.setRaw(message);
            parse.setRoomId(this.roomId);
            messageQueueExecutor.addDanmu(parse);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("弹幕监听器已停止，房间号：{},{}", roomId, reason);
        stopHeartBeat();
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket连接发生错误，房间号：{}", roomId, ex);
    }

    @SneakyThrows
    @Override
    public void onMessage(ByteBuffer bytes) {
        while (bytes.hasRemaining()) {
            int packageLen = bytes.getInt();
            short headLength = bytes.getShort();
            short ver = bytes.getShort();
            int opt = bytes.getInt();
            int seq = bytes.getInt();
            // 提取消息体
            byte[] contentBytes = new byte[packageLen - headLength];
            bytes.get(contentBytes);
            // 处理压缩数据
            if (BiliConst.Version.ZIP == ver) {
                onMessage(ByteBuffer.wrap(ZipUtil.unZlib(contentBytes)));
            } else if (BiliConst.Version.BROTLI == ver) {
                onMessage(ByteBuffer.wrap(BrotliUtils.decompress(contentBytes)));
            } else {
                // 未压缩数据直接处理
                if (BiliConst.WSOpt.HEARTBEAT_REPLY == opt) {
                    // 处理心跳回复（人气值）- 内容是4字节整数
                    int popularity = ByteBuffer.wrap(contentBytes).getInt();
                    log.debug("收到服务器心跳回复，房间号：{}，人气值：{}", roomId, popularity);
                    continue;
                }
                String content = new String(contentBytes, StandardCharsets.UTF_8);
                if (BiliConst.WSOpt.AUTH_REPLY == opt) {
                    log.debug("房间 {} 的鉴权回复：{}", roomId, content);
                    startHeartBeat();
                } else if (BiliConst.WSOpt.SEND_SMS_REPLY == opt) {
                    onMessage(content);
                } else {
                    log.debug("未处理的操作码：{}，内容：{}", opt, content);
                }
            }
        }
    }

    /**
     * 发送认证包
     */
    @SneakyThrows
    public byte[] generateAuthPack() {
        JSONObject jo = new JSONObject();
        jo.put("uid", uid);
        jo.put("roomid", roomId);
        jo.put("protover", 1);
        jo.put("platform", "web");
        jo.put("type", 2);
        jo.put("key", token);
        return pack(jo.toString(), BiliConst.WSOpt.AUTH);
    }

    @SneakyThrows
    public void sendHeartBeatPack() {
        if (isClosed()) {
            log.warn("WebSocket已关闭，跳过心跳");
            return;
        }
        send(pack("", BiliConst.WSOpt.HEARTBEAT));
        log.debug("已发送心跳包到房间：{}", this.roomId);
    }

    private void startHeartBeat() {
        stopHeartBeat();
        heartbeatFuture = scheduler.scheduleAtFixedRate(this::sendHeartBeatPack, 0, BiliConst.WSOpt.DEFAULT_HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
        log.info("心跳任务已启动");
    }

    private void stopHeartBeat() {
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(false);
            heartbeatFuture = null;
        }
        log.info("心跳任务已停止");
    }

    @SneakyThrows
    public static byte[] pack(String jsonStr, short code) {
        byte[] contentBytes = new byte[0];
        if (BiliConst.WSOpt.AUTH == code) {
            contentBytes = jsonStr.getBytes();
        } else if (BiliConst.WSOpt.HEARTBEAT == code) {
            // 心跳包内容为空
            contentBytes = new byte[0];
        }
        try (ByteArrayOutputStream data = new ByteArrayOutputStream();
             DataOutputStream stream = new DataOutputStream(data)) {
            // 封包总大小
            stream.writeInt(contentBytes.length + 16);
            // 头部长度 header的长度，固定为16
            stream.writeShort(16);
            stream.writeShort(BiliConst.Version.NORMAL);
            // 操作码（封包类型）
            stream.writeInt(code);
            // 保留字段，可以忽略。
            stream.writeInt(1);
            if (BiliConst.WSOpt.AUTH == code) {
                stream.writeBytes(jsonStr);
            }
            // 对于心跳包，不需要写入内容
            return data.toByteArray();
        }
    }

    public static final class Builder {
        private Long roomId;
        private String cookie;
        private LiveDanmuInfo liveDanmuInfo;
        private MessageQueueExecutor messageQueueExecutor;
        private ScheduledExecutorService scheduler;

        public Builder() {
        }

        public Builder roomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder cookie(String cookie) {
            this.cookie = cookie;
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

        public LiveDanmuClient build() {
            long uid = 0L;
            liveDanmuInfo = BiliClient.getLiveDanmuServer(cookie, roomId);
            if (StrUtil.isNotBlank(cookie)) {
                BiUserInfo info = BiliClient.getUserInfo(cookie);
                if (info == null) {
                    this.cookie = null;
                    uid = 0L;
                    log.warn("用户未登录，请登录后再次尝试");
                } else {
                    uid = info.getUid();
                }
            }
            int hostCount = liveDanmuInfo.getHostList().size();
            log.debug("获取房间{}的弹幕服务器{}", roomId, liveDanmuInfo.getHostList());
            // 随机选择一个服务器
            int randomHostIndex = (int) (Math.random() * hostCount);
            LiveDanmuInfo.HostInfo host = liveDanmuInfo.getHostList().get(randomHostIndex);
            LiveDanmuClient liveDanmuClient = new LiveDanmuClient(URI.create(host.getWssUrl()), roomId, cookie, uid, liveDanmuInfo.getToken());
            liveDanmuClient.messageQueueExecutor = this.messageQueueExecutor;
            liveDanmuClient.scheduler = this.scheduler;
            return liveDanmuClient;
        }
    }


}