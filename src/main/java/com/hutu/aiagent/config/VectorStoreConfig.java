package com.hutu.aiagent.config;

import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class VectorStoreConfig {
    
    @SneakyThrows
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel,@Value("classpath:rag/jipiao.txt") Resource termsOfServiceDocs) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // 读取文本内容
        String content = StreamUtils.copyToString(termsOfServiceDocs.getInputStream(), StandardCharsets.UTF_8);
        // 添加文档到向量库
        vectorStore.add(List.of(Document.builder().text(content).build()));
        return vectorStore;
    }
}