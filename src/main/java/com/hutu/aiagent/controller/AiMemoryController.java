package com.hutu.aiagent.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 会话记忆历史功能
 * 需要使用到AI配置中的 MessageChatMemoryAdvisor.builder(chatMemory).build()
 * 默认是内存存储
 */
@RestController
@Slf4j
public class AiMemoryController {
    @Resource
    private ChatClient chatClient;
    @Resource
    private ChatMemory chatMemory;

    /**
     * 带有记忆功能
     * @param query
     * @param chatId
     * @return
     */
    @GetMapping("/call")
    public Flux<String> call(@RequestParam(value = "query") String query,
                             @RequestParam(value = "chatId") String chatId
    ) {
        return chatClient.prompt(query)
                .advisors(
                        a -> a.param(CONVERSATION_ID, chatId)
                ).stream()
                .content();
    }

    /**
     * 获取根据会话ID聊天记录
     * @param chatId
     * @return
     */
    @GetMapping("{chatId}")
    public List<Message> getChatHistory(@PathVariable("chatId") String chatId) {
        List<Message> messages = chatMemory.get(chatId);
        for (Message message : messages) {
            log.info("message: {}", message);
        }
        return messages;
    }
}
