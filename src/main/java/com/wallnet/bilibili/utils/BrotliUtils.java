package com.wallnet.bilibili.utils;

import cn.hutool.core.io.FileUtil;
import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.Decoder;
import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import com.aayushatharva.brotli4j.decoder.DirectDecompress;

import java.io.File;
import java.io.IOException;

public class BrotliUtils {

    static {
        // 加载原生库
        Brotli4jLoader.ensureAvailability();
    }

    /**
     * 对Brotli压缩文件进行解压
     *
     * @param filePath 文件路径
     * @return 解压后的二进制数据
     * @throws IOException 异常
     */
    public static byte[] decompress(String filePath) throws IOException {
        File file = new File(filePath);
        return decompress(file);
    }

    /**
     * 对Brotli压缩文件进行解压
     *
     * @param file 文件对象
     * @return 解压后的二进制数据
     * @throws IOException 异常
     */
    public static byte[] decompress(File file) throws IOException {
        if (!FileUtil.exist(file)) {
            throw new IllegalArgumentException("file not found!");
        }
        byte[] data = FileUtil.readBytes(file);
        return decompress(data);
    }

    /**
     * 对Brotli压缩文件进行解压
     *
     * @param data 压缩的二进制数据
     * @return 解压后的二进制数据
     * @throws IOException 异常
     */
    public static byte[] decompress(byte[] data) throws IOException {
        DirectDecompress decompressResult = Decoder.decompress(data);
        if (decompressResult.getResultStatus() != DecoderJNI.Status.DONE) {
            throw new IllegalStateException("Some Error Occurred While Decompressing");
        }
        return decompressResult.getDecompressedData();
    }

}
