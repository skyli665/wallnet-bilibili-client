package com.wallnet.bilibili.handler;

import com.wallnet.bilibili.common.enums.DanmuCmdEnums;
import com.wallnet.bilibili.common.enums.OpenLiveCmdEnums;
import com.wallnet.bilibili.response.Danmu;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class MessageQueueExecutor {

    private static final int DEFAULT_QUEUE_CAPACITY = 10000;
    private static final int DEFAULT_POLL_TIMEOUT_MS = 1000;

    private final BlockingQueue<Danmu> queue;
    private final ExecutorService executorService;
    private final OpenLiveMessageHandler messageHandler;
    @Getter
    private volatile boolean running = false;

    public MessageQueueExecutor(OpenLiveMessageHandler messageHandler) {
        this(DEFAULT_QUEUE_CAPACITY, messageHandler);
    }

    public MessageQueueExecutor(int queueCapacity, OpenLiveMessageHandler messageHandler) {
        this(queueCapacity, messageHandler, Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "danmu-consumer-thread");
            t.setDaemon(true);
            return t;
        }));
    }

    public MessageQueueExecutor(int queueCapacity, OpenLiveMessageHandler messageHandler, ExecutorService executorService) {
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.executorService = executorService;
        this.messageHandler = messageHandler;
    }

    public synchronized void start() {
        if (running) {
            log.warn("MessageQueueExecutor is already running");
            return;
        }
        running = true;
        executorService.submit(this::consume);
        log.info("MessageQueueExecutor started");
    }

    public synchronized void stop() {
        if (!running) {
            log.warn("MessageQueueExecutor is not running");
            return;
        }
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Interrupted while stopping executor service", e);
        }
        log.info("MessageQueueExecutor stopped");
    }

    public boolean addDanmu(Danmu danmu) {
        if (danmu == null) {
            log.warn("Attempted to add null Danmu to queue");
            return false;
        }
        boolean offered = queue.offer(danmu);
        if (!offered) {
            log.warn("Queue is full, Danmu dropped: {}", danmu.getCmd());
        }
        return offered;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getQueueCapacity() {
        return queue.remainingCapacity() + queue.size();
    }

    private void consume() {
        log.info("Consumer thread started, waiting for Danmu messages...");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Danmu danmu = queue.poll(DEFAULT_POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (danmu != null) {
                    processDanmu(danmu);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Consumer thread interrupted, exiting");
                break;
            } catch (Exception e) {
                log.error("Error consuming Danmu from queue", e);
            }
        }
    }

    private void processDanmu(Danmu danmu) {
        String cmd = danmu.getCmd();
        if (messageHandler == null) {
            log.warn("MessageQueueExecutor is not initialized with a message handler");
            return;
        }
        log.debug("消息原文：{}", danmu.getRaw());
        OpenLiveCmdEnums cmdEnum1 = OpenLiveCmdEnums.getByCode(cmd);
        if (cmdEnum1 != null) {
            cmdEnum1.handle(messageHandler, danmu);
            return;
        }
        DanmuCmdEnums cmdEnum2 = DanmuCmdEnums.getByCode(cmd);
        if (cmdEnum2 != null) {
            cmdEnum2.handle(messageHandler, danmu);
            return;
        }
        log.warn("未知的cmd: {}", cmd);
    }
}