package com.wallnet.bilibili.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author skyli665
 * @date 2025-10-18 22:13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BiliConst {
    
    private static final String CORE_API_URL = "https://api.bilibili.com";
    private static final String LOGIN_API_URL = "https://passport.bilibili.com";
    private static final String LIVE_API_URL = "https://api.live.bilibili.com";
    private static final String WWW_API_URL = "https://www.bilibili.com";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Safari/605.1.15";
    public static final int[] MIXIN_KEY_ENC_TAB = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };
    
    public static final class Service {
        // 获取用户信息
        public static final String GET_USER_INFO = CORE_API_URL + "/x/space/myinfo";
        // 获取用户实名状态
        public static final String GET_USER_REAL_NAME_STATUS = CORE_API_URL + "/x/member/realname/status";
        // 获取用户实名认证信息
        public static final String GET_USER_REAL_NAME_INFO = CORE_API_URL + "/x/member/realname/apply/status";
        // 发送私信
        public static final String SEND_PRIVATE_MESSAGE = "https://api.vc.bilibili.com/web_im/v1/web_im/send_msg";
        // 获取当月舰长数
        public static final String GET_MONTH_VIP = LIVE_API_URL + "/xlive/app-room/v2/guardTab/topListNew";
        // 获取指定用户直播间信息
        public static final String FIND_USER_ROOM = LIVE_API_URL + "/room/v1/Room/getRoomInfoOld";
        // 获取动态赞或转发列表
        public static final String REACTION_DETAIL = CORE_API_URL + "/x/polymer/web-dynamic/v1/detail/reaction";
        // 获取用户船员数量
        public static final String GET_USER_SHIP = LIVE_API_URL + "/xlive/web-ucenter/user/sailors";
        // 获取直播间初始化信息
        public static final String GET_ROOM_INIT_INFO = LIVE_API_URL + "/room/v1/Room/room_init";
        // 获取直播间信息
        public static final String GET_ROOM_INFO = LIVE_API_URL + "/room/v1/Room/get_info";
        // 获取主播信息
        public static final String GET_LIVE_USER_INFO = LIVE_API_URL + "/live_user/v1/Master/info";
        // 获取直播流信息
        public static final String GET_LIVE_STREAM_INFO = LIVE_API_URL + "/xlive/web-room/v2/index/getRoomPlayInfo";
        // 获取弹幕服务器地址
        public static final String GET_DANMU_SERVER_URL = LIVE_API_URL + "/xlive/web-room/v1/index/getDanmuInfo";
        // 获取WBI信息
        public static final String GET_WBI_INFO = CORE_API_URL + "/x/web-interface/nav";
    }
    
    public static final class Login {
        public static final String GENERATE_QR_CODE = LOGIN_API_URL + "/x/passport-login/web/qrcode/generate";
        public static final String GENERATE_QR_CODE_2 = LOGIN_API_URL + "/qrcode/getLoginUrl";
        public static final String GET_CODE_STATUS = LOGIN_API_URL + "/x/passport-login/web/qrcode/poll";
        public static final String GET_TOKEN_HTML = WWW_API_URL + "/correspond/1/";
        public static final String REFRESH_TOKEN = LOGIN_API_URL + "/x/passport-login/web/cookie/refresh";
    }
    
    public static final class RSA {
        public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLgd2OAkcGVtoE3ThUREbio0Eg\n" +
                "Uc/prcajMKXvkCKFCWhJYJcLkcM2DKKcSeFpD/j6Boy538YXnR6VhcuUJOhH2x71\n" +
                "nzPjfdTcqMz7djHum0qSZA0AyCBDABUqCrfNgCiJ00Ra7GmRj+YCK1NJEuewlb40\n" +
                "JNrRuoEUXpabUzGB8QIDAQAB\n" +
                "-----END PUBLIC KEY-----";
    }
    
    public static final class WSOpt {
        //	客户端发送的心跳包(30秒发送一次)
        public static final short HEARTBEAT = 2;
        //	服务器收到心跳包的回复 人气值，数据不是JSON，是4字节整数
        public static final short HEARTBEAT_REPLY = 3;
        //	服务器推送的弹幕消息包
        public static final short SEND_SMS_REPLY = 5;
        // 客户端发送的鉴权包(客户端发送的第一个包)
        public static final short AUTH = 7;
        // 服务器收到鉴权包后的回复
        public static final short AUTH_REPLY = 8;
    }
    
    public static final class Version {
        // Body实际发送的数据——普通JSON数据
        public static final short NORMAL = 0;
        // Body中是经过压缩后的数据，请使用zlib解压，然后按照Proto协议去解析。
        public static final short ZIP = 2;

        // Body中是经过brotli压缩后的数据，请使用brotli解压，然后按照Proto协议去解析。
        public static final short BROTLI = 3;
    }
    
}
