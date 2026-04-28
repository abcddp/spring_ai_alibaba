package com.abc.controller;

import com.abc.advisor.AbcCallAdvisor1;
import com.abc.advisor.AbcCallAdvisor2;
import com.abc.advisor.SimpleMessageChatMemoryAdvisor;
import com.abc.entity.Book;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@RestController
@RequestMapping("/chatclient")
public class ZhipuChatClientController {
    private final ChatClient chatClient;

    public ZhipuChatClientController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/simple")
    public String simpleChat(@RequestParam(name = "query") String query) {

        ZhiPuAiChatOptions zhiPuAiChatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.5")
                .temperature(0.0)
                .maxTokens(100)
                .build();


        return chatClient.prompt()
                .system("你是一个AI助手")
                .user(query)
                .options(zhiPuAiChatOptions)
                .call()
                .content();
    }

    @GetMapping("/chat_response")
    public ChatResponse chatResponse(@RequestParam(name = "query") String query) {

        ZhiPuAiChatOptions zhiPuAiChatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.5")
                .temperature(0.0)
                .maxTokens(100)
                .build();


        return chatClient.prompt()
                .system("你是一个AI助手")
                .user(query)
                .options(zhiPuAiChatOptions)
                .call()
                .chatResponse();
    }

    @GetMapping("/entity")
    public Book getBook() {
        //框架会在message后面自动加上把结果转化成entity的说明
        return chatClient.prompt()
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .call()
                .entity(Book.class);
    }

    @GetMapping("/stream")
    public Flux<String> getBookStream() {
        //entity不能stream
        return chatClient.prompt()
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .stream()
                .content();
    }

    @GetMapping("/advisor")
    public Book advisor() {
        return chatClient.prompt()
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .advisors(new AbcCallAdvisor1(), new AbcCallAdvisor2())
                .call()
                .entity(Book.class);
//         com.abc.advisor.AbcCallAdvisor2          : adviseCall 2请求
//         com.abc.advisor.AbcCallAdvisor1          : adviseCall 1请求
//         com.abc.advisor.AbcCallAdvisor1          : adviseCall 1响应
//         com.abc.advisor.AbcCallAdvisor2          : adviseCall 2响应
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

}
