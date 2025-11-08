package com.bit.ai.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.protocol.Protocol;
import com.bit.ai.service.AiChatService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Datetime: 2025年11月07日16:46
 * @Author: Eleven52AC
 * @Description:
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    @Value("${ai.qwen.api-key}")
    private String apiKey;

    @Value("${ai.qwen.model}")
    private String model;

    // 临时存储会话上下文（你可以后面换成 Redis 或数据库）
    private final Map<String, List<Message>> sessionMemory = new HashMap<>();

    /**
     * 通用对话接口
     */
    public String chatWithQwen(String sessionId, String userMessage) {
        try {
            Generation gen = new Generation(Protocol.HTTP.getValue(), "https://dashscope.aliyuncs.com/api/v1");

            // 1. 取出当前会话的历史消息
            List<Message> messages = sessionMemory.computeIfAbsent(sessionId, k -> new ArrayList<>());

            // 2. 若第一次对话，加入系统提示
            if (messages.isEmpty()) {
                messages.add(Message.builder()
                        .role(Role.SYSTEM.getValue())
                        .content("你是一个有经验的 Java 工程师，善于分析异常、回答开发问题。")
                        .build());
            }

            // 3. 加入用户消息
            messages.add(Message.builder()
                    .role(Role.USER.getValue())
                    .content(userMessage)
                    .build());

            // 4. 发送请求
            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            String reply = result.getOutput().getChoices().get(0).getMessage().getContent();

            // 5. 记录 AI 回复
            messages.add(Message.builder()
                    .role(Role.ASSISTANT.getValue())
                    .content(reply)
                    .build());

            return reply;

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 调用失败：" + e.getMessage();
        }
    }

}
