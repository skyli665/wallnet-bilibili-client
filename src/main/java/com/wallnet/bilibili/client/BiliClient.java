package com.wallnet.bilibili.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.wallnet.bilibili.common.BiliConst;
import com.wallnet.bilibili.request.LiveQuery;
import com.wallnet.bilibili.request.Sender;
import com.wallnet.bilibili.response.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.math.BigInteger;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import static com.wallnet.bilibili.common.BiliConst.USER_AGENT;

/**
 * @author skyli665
 * @date 2025-10-18 22:15
 */
@Slf4j
public class BiliClient {

    private static final String[] EXCLUDE_FIELDS = {"Domain", "Path", "Expires", "HttpOnly"};

    /**
     * 获取登录二维码<br>
     * 对于url参数,需要手动生成一个qrcode,供手机扫码登录
     *
     * @return {@link com.wallnet.bilibili.response.QrCode} Qrcode
     */
    public static QrCode getQrCode() {
        QrCode qrCode = doGet(BiliConst.Login.GENERATE_QR_CODE, null, null, QrCode.class);
        if (StrUtil.isBlank(qrCode.getQrcodeKey())) {
            String qrcodeKey = HttpUtil.decodeParamMap(qrCode.getUrl(), StandardCharsets.UTF_8).get("qrcode_key");
            qrCode.setQrcodeKey(qrcodeKey);
        }
        return qrCode;
    }

    /**
     * 获取登录状态
     *
     * @param {@link String} qrcodeKey
     * @return {@link com.wallnet.bilibili.response.LoginStatus}
     */
    public static LoginStatus loginStatus(String qrcodeKey) {
        Map<String, Object> query = new HashMap<>(1);
        query.put("qrcode_key", qrcodeKey);
        BiliResult result = doGet(BiliConst.Login.GET_CODE_STATUS, null, HttpUtil.toParams(query), BiliResult.class);
        LoginStatus loginResponse = (LoginStatus) result.getData(LoginStatus.class);
        if (loginResponse.isSuccess()) {
            List<String> cookie = result.getCookie();
            String join = java.lang.String.join(";", cookie);
            loginResponse.setCookies(join);
        }
        return loginResponse;
    }

    /**
     * 获取用户信息
     *
     * @param {@link String} cookie
     * @return {@link com.wallnet.bilibili.response.BiUserInfo}
     */
    public static BiUserInfo getUserInfo(String cookie) {
        Map<String, String> header = new HashMap<>(1);
        header.put("cookie", cookie);
        return doGet(BiliConst.Service.GET_USER_INFO, header, null, BiUserInfo.class);
    }

    /**
     * 刷新token
     *
     * @param {@link String} cookie
     * @param {@link String} refreshToken
     * @return {@link com.wallnet.bilibili.response.LoginStatus}
     */
    public static LoginStatus getRefreshToken(String cookie, String refreshToken) {
        String correspondPath = getCorrespondPath(java.lang.String.format("refresh_%d", System.currentTimeMillis())
                , BiliConst.RSA.PUBLIC_KEY);

        // 获取到的是html
        HttpResponse response = HttpRequest.get(BiliConst.Login.GET_TOKEN_HTML + correspondPath)
                .addHeaders(getDefaultHeader())
                .cookie(cookie)
                .execute();
        if (response.isOk()) {
            Document document = Jsoup.parse(response.body());
            // 获取<div id="1-name">中的值
            Elements elements = document.select("#1-name");
            Map<String, Object> params = new HashMap<>();
            if (CollectionUtil.isNotEmpty(elements)) {
                params.put("refresh_csrf", elements.text());
            }
            // 转换cookie为key-value获取cookies中的bili_jct字段
            String csrf = Arrays.stream(cookie.split(";"))
                    .filter(c -> c.contains("bili_jct"))
                    .findFirst()
                    .map(c -> c.split("=")[1])
                    .orElse("");
            params.put("csrf", csrf);
            params.put("source", "main_web");
            params.put("refresh_token", refreshToken);
            Map<String, String> header = new HashMap<>(1);
            header.put("cookie", cookie);
            BiliResult result = doPostForm(BiliConst.Login.REFRESH_TOKEN, header, params, BiliResult.class);
            LoginStatus loginResponse = (LoginStatus) result.getData(LoginStatus.class);
            if (result.isSuccess()) {
                List<String> newCookie = result.getCookie();
                String join = java.lang.String.join(";", newCookie);
                loginResponse.setCookies(join);
            }
            return loginResponse;
        }
        return null;
    }

    /**
     * 获取动态赞或转发列表
     *
     * @param {@link String} cookie
     * @param {@link String} offset
     * @return {@link com.wallnet.bilibili.response.Reaction}
     */
    public static Reaction getReactionDetail(String cookie, String offset, String id) {
        Map<String, String> header = new HashMap<>(1);
        header.put("cookie", cookie);
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("offset", offset);
        params.put("web_location", "");
        return doGet(BiliConst.Service.REACTION_DETAIL, header, HttpUtil.toParams(params), Reaction.class);
    }

    /**
     * 发送私信
     *
     * @param {@link String} cookie
     * @param {@link com.wallnet.bilibili.request.Sender} sender
     * @return {@link Boolean}
     */
    public static boolean sendPrivateMessage(String cookie, Sender sender) {
        log.info("===>用户发送私信消息,data:{}", sender);
        String csrf = Arrays.stream(cookie.split(";"))
                .filter(c -> c.contains("bili_jct"))
                .findFirst()
                .map(c -> c.split("=")[1])
                .orElse("");
        Map<String, String> header = new HashMap<>(3);
        header.put("cookie", cookie);
        header.put("Referer", "https://message.bilibili.com/");
        Map<String, Object> data = new HashMap<>();
        data.put("msg[sender_uid]", sender.getSenderId());
        data.put("msg[receiver_id]", sender.getReceiverId());
        data.put("msg[receiver_type]", "1");
        data.put("msg[msg_type]", "1");
        data.put("msg[msg_status]", "0");
        data.put("msg[dev_id]", UUID.randomUUID().toString());
        // 时间戳(秒)
        data.put("msg[timestamp]", String.valueOf(System.currentTimeMillis()).substring(0, 10));
        data.put("msg[content]", "{\"content\":\"" + sender.getContent() + "\"}");
        data.put("msg[new_face_version]", "1");
        data.put("mobi_app", "web");
        data.put("from_firework", "0");
        data.put("build", "0");
        data.put("csrf_token", csrf);
        data.put("csrf", csrf);
        BiliResult biliResult = doPostForm(BiliConst.Service.SEND_PRIVATE_MESSAGE, header, data, BiliResult.class);
        return biliResult.isSuccess();
    }

    /**
     * 获取用户房间信息
     *
     * @param {@link Integer} uid
     * @return {@link com.wallnet.bilibili.response.Room}
     */
    public static Room getRoom(Long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("mid", uid);
        return doGet(BiliConst.Service.FIND_USER_ROOM, null, HttpUtil.toParams(params), Room.class);
    }


    /**
     * 获取用户当月舰长
     *
     * @param {@link String} cookie
     * @param {@link com.wallnet.bilibili.request.LiveQuery} query
     * @return {@link com.wallnet.bilibili.response.Room}
     */
    public static Room getUserMonthVip(String cookie, LiveQuery query) {
        log.info("===>获取用户当月舰长");
        Map<String, String> header = new HashMap<>(1);
        header.put("cookie", cookie);
        Map<String, Object> params = new HashMap<>();
        params.put("roomid", query.getRoomId());
        params.put("page", query.getPageIndex());
        params.put("ruid", query.getUid());
        params.put("page_size", query.getPageSize());
        params.put("typ", "3");
        params.put("platform", "web");
        return doGet(BiliConst.Service.GET_MONTH_VIP, header, HttpUtil.toParams(params), Room.class);
    }

    public static Sailors getSailors(String cookie, int page) {
        log.info("===>获取用户舰长");
        Map<String, String> header = new HashMap<>(1);
        header.put("cookie", cookie);
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        return doGet(BiliConst.Service.GET_USER_SHIP, header, HttpUtil.toParams(params), Sailors.class);
    }

    /**
     * 获取房间初始化信息
     *
     * @param {@link Long} roomId
     * @return {@link com.wallnet.bilibili.response.RoomInitInfo}
     */
    public static RoomInitInfo getRoomInitInfo(Long roomId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", roomId);
        return doGet(BiliConst.Service.GET_ROOM_INIT_INFO, null, HttpUtil.toParams(params), RoomInitInfo.class);
    }

    /**
     * 获取房间信息
     *
     * @param {@link Long} realRoomId
     * @param {@link String} roomTitle
     * @return {@link com.wallnet.bilibili.response.RoomInfo}
     */
    public static RoomInfo getRoomInfo(Long realRoomId, String roomTitle) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", realRoomId);
        Map<String, String> header = new HashMap<>(1);
        header.put("Referer", "https://live.bilibili.com/" + (roomTitle != null ? roomTitle : ""));
        return doGet(BiliConst.Service.GET_ROOM_INFO, header, HttpUtil.toParams(params), RoomInfo.class);
    }

    /**
     * 获取主播信息
     *
     * @param uid
     * @param roomTitle
     * @return
     */
    public static LiveUserInfo getLiveUserInfo(Long uid, String roomTitle) {
        Map<String, String> header = new HashMap<>(1);
        header.put("Referer", "https://live.bilibili.com/" + (roomTitle != null ? roomTitle : ""));
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return doGet(BiliConst.Service.GET_LIVE_USER_INFO, header, HttpUtil.toParams(params), LiveUserInfo.class);
    }

    /**
     * 获取直播流信息
     *
     * @param qn
     * @param roomId
     * @param roomTitle
     * @return
     */
    public static RoomInitInfo getLiveStream(Integer qn, Long roomId, String roomTitle) {
        Map<String, String> header = new HashMap<>(1);
        header.put("Referer", "https://live.bilibili.com/" + (roomTitle != null ? roomTitle : ""));
        Map<String, Object> params = new HashMap<>();
        params.put("protocol", 0);
        params.put("format", 0);
        params.put("codec", 0);
        params.put("qn", qn);
        params.put("room_id", roomId);
        return doGet(BiliConst.Service.GET_LIVE_STREAM_INFO, header, HttpUtil.toParams(params), RoomInitInfo.class);
    }

    /**
     * 获取弹幕服务器url
     *
     * @param cookie
     * @param roomId
     * @return
     */
    public static LiveDanmuInfo getLiveDanmuServer(String cookie, Long roomId) {
        log.info("===>获取弹幕服务器url");
        Map<String, String> header = getDefaultHeader();
        header.put("Referer", "https://live.bilibili.com/");
        header.put("cookie", cookie);
        Map<String, Object> params = new HashMap<>();
        params.put("type", 0);
        params.put("id", roomId);
        params.put("web_location", "444.8");
        Map<String, Object> wbiSignature = generateWbiSignature(params);
        return doGet(BiliConst.Service.GET_DANMU_SERVER_URL, header, HttpUtil.toParams(wbiSignature), LiveDanmuInfo.class);
    }

    /**
     * 获取wbi信息
     *
     * @return
     */
    public static BiliWbiInfo getWbiInfo() {
        log.info("===>获取wbi信息");
        Map<String, String> header = getDefaultHeader();
        return doGet(BiliConst.Service.GET_WBI_INFO, header, null, BiliWbiInfo.class);
    }

    @SneakyThrows
    private static <T> T doPostBody(String url, Map<String, String> headers, String body, Class<T> clazz) {
        Map<String, String> header = getDefaultHeader();
        if (CollectionUtil.isNotEmpty(headers)) {
            header.putAll(headers);
        }
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(header)
                .body(body)
                .execute();
        return doReturn(url, body, clazz, response);
    }

    @SneakyThrows
    private static <T> T doPostForm(String url, Map<String, String> headers, Map<String, Object> params, Class<T> clazz) {
        Map<String, String> header = getDefaultHeader();
        if (CollectionUtil.isNotEmpty(headers)) {
            header.putAll(headers);
        }
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(header)
                .form(params)
                .execute();
        return doReturn(url, params, clazz, response);
    }

    @SneakyThrows
    private static <T> T doGet(String url, Map<String, String> headers, String params, Class<T> clazz) {
        Map<String, String> header = getDefaultHeader();
        if (CollectionUtil.isNotEmpty(headers)) {
            header.putAll(headers);
        }
        String finalUrl = url;
        if (StrUtil.isNotBlank(params)) {
            finalUrl += "?" + params;
        }
        HttpResponse response = HttpRequest.get(finalUrl).addHeaders(headers).execute();
        return doReturn(url, params, clazz, response);
    }

    private static Map<String, String> getDefaultHeader() {
        Map<String, String> header = new HashMap<>(6);
        header.put("User-Agent", USER_AGENT);
        header.put("Accept", "*/*");
        header.put("Referer", "https://www.bilibili.com/");
        header.put("Connection", "keep-alive");
        return header;
    }

    private static List<String> getCookies(List<HttpCookie> httpCookies) {
        if (CollectionUtil.isNotEmpty(httpCookies)) {
            return httpCookies.stream()
                    .filter(cookie -> !ArrayUtil.contains(EXCLUDE_FIELDS, cookie.getName()))
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    private static <T> T doReturn(String url, Object params, Class<T> clazz, HttpResponse response) {
        String body = response.body();
        List<String> httpCookies = getCookies(response.getCookies());
        log.debug("\r\n===>bili client start\r\n>请求接口: {}\r\n>参数: {}\r\n>cookies:{}\r\n>返回结果: {}\r\n====end====\r\n", url, params, httpCookies, body);
        BiliResult result = BiliResult.create(body);
        result.setCookie(httpCookies);
        if (!BiliResult.class.equals(clazz)) {
            return (T) result.getData(clazz);
        }
        // 直接返回结果
        return (T) result;
    }

    @SneakyThrows
    private static String getCorrespondPath(String plaintext, String publicKeyStr) {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKeyStr = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .trim();
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        String algorithm = "RSA/ECB/OAEPPadding";
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Encode the plaintext to bytes
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);

        // Add OAEP padding to the plaintext bytes
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
        // Encrypt the padded plaintext bytes
        byte[] encryptedBytes = cipher.doFinal(plaintextBytes);
        // Convert the encrypted bytes to a Base64-encoded string
        return new BigInteger(1, encryptedBytes).toString(16);
    }


    private static String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++)
            key.append(s.charAt(BiliConst.MIXIN_KEY_ENC_TAB[i]));
        return key.toString();
    }

    /**
     * 生成B站WBI签名参数
     *
     * @param params 原始参数Map
     * @return 添加了wts和w_rid的参数Map
     */
    public static Map<String, Object> generateWbiSignature(Map<String, Object> params) {
        try {
            BiliWbiInfo wbiInfo = BiliClient.getWbiInfo();
            String imgKey = wbiInfo.getImgKey();
            String subKey = wbiInfo.getSubKey();
            String mixinKey = getMixinKey(imgKey, subKey);
            long wts = System.currentTimeMillis() / 1000;
            params.put("wts", wts);
            // 按照键名排序
            TreeMap<String, Object> sortedParams = new TreeMap<>(params);
            String param = HttpUtil.toParams(sortedParams);
            String s = param + mixinKey;
            // 计算MD5哈希值
            String wbiSign = DigestUtil.md5Hex(s);
            // 添加w_rid参数
            params.put("w_rid", wbiSign);
        } catch (Exception e) {
            log.error("生成WBI签名失败", e);
        }
        return params;
    }
}