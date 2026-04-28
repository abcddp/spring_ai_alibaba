package com.abc.controller;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/zhipu")
public class ZhipuChatController {

    private final ChatModel chatModel;

    public ZhipuChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @GetMapping("/simple")
    public String simpleChat(@RequestParam(name = "query") String query) {
        // 调用ChatModel 的call方法传入问题完成模型调用return chatModel.call(query);
        return chatModel.call(query);
    }

    @GetMapping("/message")
    public String message(@RequestParam(name = "query") String query) {
        SystemMessage systemMessage = new SystemMessage("你是一个AI助手");
        UserMessage userMessage = new UserMessage(query);
        return chatModel.call(systemMessage, userMessage);
    }

    @GetMapping("/chatOptions")
    public String chatOptions(@RequestParam(name = "query") String query) {
        SystemMessage systemMessage = new SystemMessage("你是一个AI助手");
        UserMessage userMessage = new UserMessage(query);
        ZhiPuAiChatOptions zhiPuAiChatOptions = new ZhiPuAiChatOptions();
        zhiPuAiChatOptions.setModel("glm-4.5");
        zhiPuAiChatOptions.setTemperature(0.0);
        zhiPuAiChatOptions.setMaxTokens(15536);
        //两种方法创建chatOption
        ZhiPuAiChatOptions zhiPuAiChatOptions1 = ZhiPuAiChatOptions.builder().model("glm-4.5").temperature(0.0).maxTokens(15536).build();

        return chatModel.call(new Prompt(List.of(systemMessage,userMessage),zhiPuAiChatOptions)).getResult().getOutput().getText();
    }

    @GetMapping("/stream/chat")
    public Flux<String> stream(@RequestParam(name = "query") String query) {
        return chatModel.stream(query);
    }


}
