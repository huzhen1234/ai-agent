package com.hutu.aiagent;

import cn.hutool.json.JSONUtil;
import com.hutu.aiagent.utils.AppDocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AiAgentApplicationTests {

    @Resource
    private AppDocumentLoader loveAppDocumentLoader;

    @Test
    void contextLoads() {
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        for (Document document : documents) {
            System.out.println(JSONUtil.toJsonStr(document));
        }

    }

}
