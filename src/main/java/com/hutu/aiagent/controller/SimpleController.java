package com.hutu.aiagent.controller;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/simple")
public class SimpleController {

    @Resource
    private SimpleVectorStore simpleVectorStore;

    private static final Logger logger = LoggerFactory.getLogger(SimpleController.class);
    private final String SAVE_PATH = System.getProperty("user.dir") + "/src/main/resources/json/test.json";


    @GetMapping("/add")
    public void importData() {
        logger.info("start add data");
        HashMap<String, Object> map = new HashMap<>();
        map.put("year", 2025);
        map.put("name", "yingzi");
        List<Document> documents = List.of(
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("year", 2024)),
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", map),
                new Document("1", "test content", map));
        simpleVectorStore.add(documents);
    }

    @GetMapping("/delete")
    public void delete() {
        logger.info("start delete data");
        simpleVectorStore.delete(List.of("1"));
    }

    @GetMapping("/save")
    public void save() {
        logger.info("start save data: {}", SAVE_PATH);
        File file = new File(SAVE_PATH);
        if (file.exists()) {
            file.delete();
        }
        simpleVectorStore.save(file);
    }

    /**
     * 加载数据 从文件中加载到向量仓库
     */
    @GetMapping("/load")
    public void load() {
        logger.info("start load data: {}", SAVE_PATH);
        File file = new File(SAVE_PATH);
        simpleVectorStore.load(file);
    }

    @GetMapping("/search")
    public List<Document> search() {
        logger.info("start search data");
        return simpleVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("Spring") // 向量相似性搜索
                .topK(2) // 搜索结果数量
                .build());
    }

    @GetMapping("/search-filter")
    public List<Document> searchFilter() {
        logger.info("start search  filter data");
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression expression = b.and(
                b.in("year", 2025, 2024),
                b.eq("name", "yingzi")
        ).build();

        return simpleVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("Spring")
                .topK(2)
                .filterExpression(expression).build());
    }

}