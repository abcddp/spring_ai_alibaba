package com.abc.controller;

import com.abc.advisor.AbcCallAdvisor1;
import com.abc.advisor.AbcCallAdvisor2;
import com.abc.advisor.SimpleMessageChatMemoryAdvisor;
import com.abc.entity.Book;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chatmemory")
public class ZhipuChatMemoryController {
    private final ChatClient chatClient;

    public ZhipuChatMemoryController(ChatClient.Builder builder) {
        // messageChatMemoryAdvisor相当于在request和response处记录用户消息和AI消息，所以必须要传入chatMemory来作为消息记录的物理载体，另外
        // MessageWindowChatMemory默认只会寸20条数据
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
//                .chatMemoryRepository() 实现ChatMemoryRepository接口来自定义消息存储在哪里
                .build();
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
        this.chatClient = builder
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
    }


    @GetMapping("/simpleMessageChatMemoryAdvisor")
    public String simpleMessageChatMemoryAdvisor(@RequestParam(name = "query") String query,
                                                 @RequestParam(name = "conversationId") String conversationId) {

        return chatClient.prompt()
                .user(query)
                //把会话id存入上下文
                .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                .advisors(new SimpleMessageChatMemoryAdvisor())
                .call()
                .content();
    }

    @GetMapping("/messageChatMemoryAdvisor")
    public String messageChatMemoryAdvisor(@RequestParam(name = "query") String query,
                                           @RequestParam(name = "conversationId") String conversationId) {

        return chatClient.prompt()
                .user(query)
                //把会话id存入上下文
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
    }

}
