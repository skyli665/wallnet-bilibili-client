package com.wallnet.bilibili.response;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.wallnet.bilibili.common.enums.LiveStatusEnum;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author skyli665
 * @date 2026-01-20 23:16
 */
@Data
public class RoomInitInfo {

    @JSONField(name = "room_id")
    private Integer roomId;
    @JSONField(name = "short_id")
    private Integer shortId;
    private Long uid;
    @JSONField(name = "need_p2p")
    private Integer needP2p;
    @JSONField(name = "is_hidden")
    private Boolean isHidden;
    @JSONField(name = "is_locked")
    private Boolean isLocked;
    @JSONField(name = "is_portrait")
    private Boolean isPortrait;
    @JSONField(name = "live_status")
    private Integer liveStatus;
    @JSONField(name = "hidden_till")
    private Integer hiddenTill;
    @JSONField(name = "lock_till")
    private Integer lockTill;
    private Boolean encrypted;
    @JSONField(name = "pwd_verified")
    private Boolean pwdVerified;
    @JSONField(name = "live_time")
    private Long liveTime;
    @JSONField(name = "room_shield")
    private Integer roomShield;
    @JSONField(name = "is_sp")
    private Integer isSp;
    @JSONField(name = "special_type")
    private Integer specialType;
    @JSONField(name = "all_special_types")
    private Integer[] allSpecialTypes;
    @JSONField(name = "playurl_info")
    private PlayurlInfo playurlInfo;
    @JSONField(name = "pure_control_function")
    private String pureControlFunction;
    @JSONField(name = "degraded_playurl")
    private String degradedPlayurl;
    @JSONField(name = "subtitle_cfg")
    private String subtitleCfg;
    @JSONField(name = "official_type")
    private Integer officialType;
    @JSONField(name = "official_room_id")
    private Integer officialRoomId;
    @JSONField(name = "risk_with_delay")
    private Integer riskWithDelay;
    @JSONField(name = "multi_screen_info")
    private String multiScreenInfo;
    private String currentQn;

    /**
     * 是否直播
     *
     * @return
     */
    public boolean isLive() {
        return LiveStatusEnum.LIVING.codeEquals(liveStatus);
    }

    public String getLiveStreamUrl() {

        List<Codec> codecs = Optional.ofNullable(this.playurlInfo)
                .map(PlayurlInfo::getPlayurl)
                .map(Playurl::getStream)
                // 获取第一个
                .map(i -> i.get(0))
                .map(Stream::getFormat)
                .map(i -> i.get(0))
                .map(Format::getCodec)
                .orElse(null);
        if (CollUtil.isNotEmpty(codecs)) {
            codecs.sort(Comparator.comparing(Codec::getCurrentQn));
            Codec codec = codecs.get(0);
            List<RoomInitInfo.UrlInfo> urlInfos = codec.getUrlInfo();
            if (urlInfos != null && !urlInfos.isEmpty()) {
                int currentQn = codec.getCurrentQn();
                this.getPlayurlInfo().getPlayurl().getGQnDesc()
                        .stream()
                        .filter(i -> currentQn == i.getQn())
                        .findAny()
                        .ifPresent(qnDesc -> this.setCurrentQn(qnDesc.getDesc()));
                RoomInitInfo.UrlInfo urlInfo = urlInfos.get(0);
                if (urlInfo.getHost() != null && codec.getBaseUrl() != null && urlInfo.getExtra() != null) {
                    return urlInfo.getHost() + codec.getBaseUrl() + urlInfo.getExtra();
                }
            }
        }
        return null;
    }

    @Data
    public static class PlayurlInfo {
        @JSONField(name = "conf_json")
        private String confJson;
        @JSONField(name = "playurl")
        private Playurl playurl;
        @JSONField(name = "expected_quality")
        private ExpectedQuality expectedQuality;
        @JSONField(name = "qn_desc_more_ab")
        private Integer qnDescMoreAb;
    }

    @Data
    public static class ExpectedQuality {
        private Integer qn;
        private Integer hdrType;
    }

    @Data
    public static class Playurl {
        @JSONField(name = "cid")
        private Long cid;
        @JSONField(name = "g_qn_desc")
        private List<QnDesc> gQnDesc;
        private List<Stream> stream;
        @JSONField(name = "p2p_data")
        private P2pData p2pData;
        @JSONField(name = "dolby_qn")
        private Integer dolbyQn;
    }

    @Data
    public static class P2pData {
        @JSONField(name = "p2p")
        private Boolean p2p;
        @JSONField(name = "p2p_type")
        private Integer p2pType;
        @JSONField(name = "m_p2p")
        private Boolean mP2p;
        @JSONField(name = "m_servers")
        private List<String> mServers;
    }

    @Data
    public static class Stream {
        @JSONField(name = "protocol_name")
        private String protocolName;
        private List<Format> format;
    }

    @Data
    public static class Format {
        @JSONField(name = "format_name")
        private String formatName;
        private List<Codec> codec;
        @JSONField(name = "master_url")
        private String masterUrl;
    }

    @Data
    public static class Codec {
        @JSONField(name = "codec_name")
        private String codecName;
        @JSONField(name = "current_qn")
        private Integer currentQn;
        @JSONField(name = "accept_qn")
        private Integer[] acceptQn;
        @JSONField(name = "base_url")
        private String baseUrl;
        @JSONField(name = "url_info")
        private List<UrlInfo> urlInfo;
        @JSONField(name = "hdr_qn")
        private Integer hdrQn;
        @JSONField(name = "dolby_type")
        private Integer dolbyType;
        @JSONField(name = "attr_name")
        private String attrName;
        @JSONField(name = "hdr_type")
        private Integer hdrType;
        @JSONField(name = "drm")
        private Boolean drm;
        @JSONField(name = "drm_key_systems")
        private Boolean drmKeySystems;
        @JSONField(name = "video_codecs")
        private VideoCodecs videoCodecs;
        @JSONField(name = "audio_codecs")
        private AudioCodecs audioCodecs;
    }

    @Data
    public static class AudioCodecs {
        private String base;
    }

    @Data
    public static class VideoCodecs {
        private String base;
    }

    @Data
    public static class UrlInfo {
        private String host;
        private String extra;
        @JSONField(name = "stream_ttl")
        private Integer streamTtl;
    }


    @Data
    public static class QnDesc {
        private Integer qn;
        private String desc;
        @JSONField(name = "hdr_desc")
        private String hdrDesc;
        @JSONField(name = "attr_desc")
        private String attrDesc;
        @JSONField(name = "hdr_type")
        private Integer hdrType;
        @JSONField(name = "media_base_desc")
        private Integer mediaBaseDesc;
    }
}
