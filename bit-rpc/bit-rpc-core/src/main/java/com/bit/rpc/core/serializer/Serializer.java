package com.bit.rpc.core.serializer;

import java.io.IOException;

/**
 * @Datetime: 2026年01月13日18:14
 * @Author: Eleven52AC
 * @Description: 序列化器接口
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;

}
