package com.hutu.aiagent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

    @Bean
    public ChatClient chatClient(DashScopeChatModel dashScopeChatModel, ChatMemory chatMemory) {
        return ChatClient
                .builder(dashScopeChatModel)
                .defaultSystem("你是一名高级开发人员，请你依照你的经验提供合理的方案")
                // 环绕增强
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),// 开启日志 此时需要配置日志级别
                        MessageChatMemoryAdvisor.builder(chatMemory).build() // 开启内存存储
                )
                .build();
    }
}
