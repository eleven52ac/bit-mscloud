package com.bit.ai.service;

/**
 * @Datetime: 2025年11月07日16:46
 * @Author: Eleven52AC
 * @Description: AI 聊天服务接口
 */
public interface AiChatService {

    /**
     *
     * @Author: Eleven52AC
     * @Description:
     * @param sessionId
     * @param message
     * @return
     */
    String chatWithQwen(String sessionId, String message);
}
