# WallNet Bilibili Client

一个基于 Java 实现的 Bilibili 直播弹幕客户端，支持弹幕接收、礼物处理、SC 消息处理等功能。

## 功能特性

- **弹幕监听**：实时接收直播间弹幕消息
- **礼物处理**：处理直播间礼物赠送消息
- **SC 消息**：支持醒目留言消息处理
- **直播录制**：支持直播内容录制
- **用户信息**：获取主播和观众信息

## 技术栈

- Java 8+
- Maven
- WebSocket
- Protobuf
- Brotli 压缩

## 项目结构

```
src/main/java/com/wallnet/bilibili/
├── client/           # 客户端模块
│   ├── BiliClient.java      # Bilibili API 客户端
│   ├── LiveDanmuClient.java # 弹幕客户端
│   └── LiveRecorder.java    # 直播录制器
├── common/           # 公共模块
│   ├── BiliConst.java       # 常量定义
│   └── enums/               # 枚举类
│       ├── DanmuCmdEnums.java
│       ├── GenderEnums.java
│       └── LiveStatusEnum.java
├── handler/          # 消息处理器
│   ├── Handler.java         # 处理器接口
│   ├── HandlerFactory.java  # 处理器工厂
│   └── impl/                # 处理器实现
│       ├── GiftHandler.java
│       ├── MessageHandler.java
│       └── ScHandler.java
├── request/          # 请求模型
├── response/         # 响应模型
└── utils/            # 工具类
    ├── BrotliUtils.java
    └── ProtobufUtil.java
```

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+

### 编译项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 使用示例

调用方式参考Test包下的测试类例。

## 消息处理器

项目采用责任链模式处理不同类型的弹幕消息：

| 处理器 | 功能 |
|--------|------|
| MessageHandler | 处理普通弹幕消息 |
| GiftHandler | 处理礼物赠送消息 |
| ScHandler | 处理醒目留言消息 |