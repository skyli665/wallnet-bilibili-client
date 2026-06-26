package com.wallnet.bilibili.client;

import cn.hutool.core.io.FileUtil;
import com.wallnet.bilibili.response.LiveTask;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.wallnet.bilibili.common.BiliConst.USER_AGENT;

/**
 * 基于Netty和NIO实现的直播录制器
 * 支持多路复用和通过roomId区分任务
 * 使用NIO的Buffer和通道实现文件写入，优先采用直接缓冲区
 *
 * @author skyli665
 * @date 2026-01-22 16:20
 */
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LiveRecorder {
    
    // 存储所有录制任务
    private static final Map<Long, LiveTask> RECORDING_TASKS = new ConcurrentHashMap<>();
    
    /**
     * 开始录制直播流
     *
     * @param liveTask
     */
    @SneakyThrows
    public static void startRecording(LiveTask liveTask) {
        Objects.requireNonNull(liveTask, "录制任务不存在");
        
        if (!liveTask.isLive()) {
            log.info("房间当前不在直播");
            return;
        }
        
        if (RECORDING_TASKS.containsKey(liveTask.getRoomId())) {
            log.info("任务已存在，请勿重复启动");
            return;
        }
        URL url = new URL(liveTask.getStreamUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Referer", "https://live.bilibili.com/");
        conn.setConnectTimeout(10000);
        // 无超时限制，因为直播可能持续很长时间
        conn.setReadTimeout(0);
        AtomicLong totalBytes = new AtomicLong(0);
        long startTime = System.currentTimeMillis();
        try (InputStream inputStream = conn.getInputStream();
             OutputStream outputStream = FileUtil.getOutputStream(liveTask.getFileName())) {
            log.info("开始录制，保存到:{} ", liveTask.getFileName());
            byte[] buffer = new byte[8192];
            long lastReportTime = System.currentTimeMillis();
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    log.info("直播流结束");
                    break;
                }
                outputStream.write(buffer, 0, bytesRead);
                totalBytes.addAndGet(bytesRead);
                // 每隔一段时间输出进度
                long currentTime = System.currentTimeMillis();
                // 5秒更新一次
                if (currentTime - lastReportTime > 5000) {
                    long duration = (currentTime - startTime) / 1000;
                    log.info(">>>录制进度: 已录制 {} 字节, 时长 {} 秒", totalBytes.get(), duration);
                    lastReportTime = currentTime;
                }
            }
            long duration = (System.currentTimeMillis() - startTime) / 1000;
            log.info(">>>录制完成: 总计录制 {} 字节, 时长 {} 秒", totalBytes.get(), duration);
        }
        RECORDING_TASKS.remove(liveTask.getRoomId());
    }
    
    
}