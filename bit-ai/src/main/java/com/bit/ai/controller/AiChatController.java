package com.bit.ai.controller;

import cn.hutool.core.lang.UUID;
import com.bit.ai.service.AiChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ai 聊天
 * @Datetime: 2025年11月07日16:47
 * @Author: Eleven52AC
 * @Description: 聊天控制器
 */
@RestController
@RequestMapping("/ai/chat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;


    /**
     * 聊天会话
     * @Author: Eleven52AC
     * @Description: 聊天会话
     * @param sessionId
     * @param message
     * @return
     */
    @PostMapping("/qwen")
    public String chat(@RequestParam(name = "sessionId", required = false) String sessionId,
                       @RequestParam(name = "message") String message) {
        // todo 可以改成策略模式，对接多个ai模型。
        if(StringUtils.isBlank(sessionId)){
            sessionId = UUID.fastUUID().toString(true);
        }
        return aiChatService.chatWithQwen(sessionId, message);
    }

}

