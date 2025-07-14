package com.hutu.aiagent.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 最简单的方式AI问答
 * 无记忆功能
 */
@RestController
@Slf4j
public class AiEasyController {
    @Resource
    private ChatClient chatClient;

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
}
