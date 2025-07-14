package com.hutu.aiagent.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 最简单的方式AI问答
 * 无记忆功能
 */
@RestController
@Slf4j
public class AiEasyController {
    @Resource
    private ChatClient chatClient;
    @Resource
    private ChatMemory chatMemory;

    /**
     * 阻塞获取结果
     * @return
     */
    @GetMapping("test")
    public String testAi(){
        return chatClient.prompt().user("你是谁").call().content();
    }


    /**
     * 流式获取结果
     * @return
     */
    @GetMapping(value = "test_stream",produces = "text/html;charset=utf-8")
    public Flux<String> testAiStream(){
        return chatClient.prompt().user("你是谁").stream().content();
    }

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
