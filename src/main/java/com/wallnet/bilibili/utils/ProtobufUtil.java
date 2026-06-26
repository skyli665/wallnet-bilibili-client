package com.wallnet.bilibili.utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 简易protobuf字段解析工具，兼容B站INTERACT_WORD_V2等pb字段。
 * 由 JavaScript 版 protobuf.js 移植
 */
public class ProtobufUtil {

    /**
     * 读取变长整数
     *
     * @param buf 字节数组
     * @param pos 起始位置
     * @return 数组，[0]为解析的值，[1]为新的位置
     */
    public static long[] readVarint(byte[] buf, int pos) {
        long result = 0;
        int shift = 0;
        byte b;
        do {
            b = buf[pos];
            pos++;
            result |= (long) (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return new long[]{result, pos};
    }

    /**
     * 解析base64编码的protobuf数据
     *
     * @param pbBase64 base64编码的protobuf数据
     * @return 字段映射，key为字段号，value为字段值
     */
    public static Map<Integer, Object> parsePb(String pbBase64) {
        byte[] buf = java.util.Base64.getDecoder().decode(pbBase64);
        int pos = 0;
        Map<Integer, Object> fields = new HashMap<>();

        while (pos < buf.length) {
            // 读取key
            long[] keyResult = readVarint(buf, pos);
            long key = keyResult[0];
            pos = (int) keyResult[1];

            // 解析字段号和wire type
            int fieldNum = (int) (key >> 3);
            int wireType = (int) (key & 0x7);

            Object value = null;
            if (wireType == 0) {
                // varint
                long[] valueResult = readVarint(buf, pos);
                value = valueResult[0];
                pos = (int) valueResult[1];
            } else if (wireType == 2) {
                // length-delimited
                long[] lengthResult = readVarint(buf, pos);
                int length = (int) lengthResult[0];
                pos = (int) lengthResult[1];

                byte[] valueBytes = new byte[length];
                System.arraycopy(buf, pos, valueBytes, 0, length);
                try {
                    value = new String(valueBytes, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    value = valueBytes;
                }
                pos += length;
            } else {
                // 其他类型暂不处理
                break;
            }

            fields.put(fieldNum, value);
        }

        return fields;
    }
}