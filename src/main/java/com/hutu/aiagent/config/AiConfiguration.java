package com.hutu.aiagent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AiConfiguration {

    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.jdbc-url}")
    private String mysqlJdbcUrl;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.username}")
    private String mysqlUsername;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.password}")
    private String mysqlPassword;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.driver-class-name}")
    private String mysqlDriverClassName;

    @Bean
    public MysqlChatMemoryRepository mysqlChatMemoryRepository() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(mysqlDriverClassName);
        dataSource.setUrl(mysqlJdbcUrl);
        dataSource.setUsername(mysqlUsername);
        dataSource.setPassword(mysqlPassword);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return MysqlChatMemoryRepository.mysqlBuilder()
                .jdbcTemplate(jdbcTemplate)
                .build();
    }

/*    *//**
     * 基于内存的记忆功能
     *//*
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }*/


    /**
     * 基于 mysql的记忆功能
     */
    @Bean
    public ChatMemory chatMemory(MysqlChatMemoryRepository mysqlChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(mysqlChatMemoryRepository)
                .build();
    }

    @Bean
    public ChatClient chatClient(DashScopeChatModel dashScopeChatModel, ChatMemory chatMemory, SimpleVectorStore simpleVectorStore) {
        return ChatClient
                .builder(dashScopeChatModel)
                .defaultSystem("""
						您是“Funnair”航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
						您正在通过在线聊天系统与客户互动。
						您能够支持已有机票的预订详情查询、机票日期改签、机票预订取消等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
						在提供有关机票预订详情查询、机票日期改签、机票预订取消等操作之前，您必须始终从用户处获取以下信息：预订号、客户姓名。
						在询问用户之前，请检查消息历史记录以获取预订号、客户姓名等信息，尽量避免重复询问给用户造成困扰。
						在更改预订之前，您必须确保条款允许这样做。
						如果更改需要收费，您必须在继续之前征得用户同意。
						使用提供的功能获取预订详细信息、更改预订和取消预订。
						如果需要，您可以调用相应函数辅助完成。
						请讲中文。

						今天的日期是 {current_date}.
					""")
                // 环绕增强
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),// 开启日志 此时需要配置日志级别
                        MessageChatMemoryAdvisor.builder(chatMemory).build(), // 开启内存存储 -- 聊天记录
//                        PromptChatMemoryAdvisor.builder(chatMemory).build(),  // 开启内存存储 -- 提示词(精简般的聊天记录，记录核心内容)
                        new QuestionAnswerAdvisor(simpleVectorStore)  // RAG 询问时，会先想RAG数据库查询
//						VectorStoreChatMemoryAdvisor.builder(simpleVectorStore).build() // 开启内存存储 -- RAG数据库
                )/*.defaultToolNames(
						"getBookingDetails",
						"changeBooking",
						"cancelBooking"
				)*/
                .build();
    }

	/**
	 * 		return this.chatClient.prompt()
	 * 				.system(s -> s.param("current_date", LocalDate.now().toString()))
	 */


    /**
     * List<Message> history = [
     *     UserMessage("我想订明天北京飞上海的航班..."),
     *     AiMessage("已找到3个航班：CA1855...")
     * ];
     *
     *
     * 存储提取后的关键信息（需自定义提取逻辑）
     * List<String> prompts = [
     *     "出发地:北京",
     *     "目的地:上海",
     *     "日期:2024-07-20",
     *     "舱位:经济舱",
     *     "偏好:靠窗",
     *     "可选航班:CA1855,MU511,HU760"
     * ];
     */
}
