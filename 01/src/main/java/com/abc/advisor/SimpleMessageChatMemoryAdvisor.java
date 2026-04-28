package com.abc.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 将发消息前加入历史消息实现记忆
 * AI的消息也加入历史消息
 */
@Slf4j
public class SimpleMessageChatMemoryAdvisor implements BaseAdvisor {


    private static Map<String, List<Message>> chatMemory = new ConcurrentHashMap<>();

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 通过会话id查询对话记录
        String conversationId = chatClientRequest.context().get("conversationId").toString();
        List<Message> historyMessage = chatMemory.computeIfAbsent(conversationId, k -> new ArrayList<>());
        // 把本次请求放在对话记录里面
        List<Message> message = chatClientRequest.prompt()
                .getInstructions();
        historyMessage.addAll(message);
        // 将历史会话放进当前request
        Prompt oldPrompt = chatClientRequest.prompt();
        Prompt newPrompt = oldPrompt.mutate()
                .messages(historyMessage)
                .build();
        // 相当于落库
        chatMemory.put(conversationId, historyMessage);
        return chatClientRequest.mutate()
                .prompt(newPrompt)
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        // 通过会话id查询对话记录
        String conversationId = chatClientResponse.context().get("conversationId").toString();
        List<Message> historyMessage = chatMemory.computeIfAbsent(conversationId, k -> new ArrayList<>());
        // 获取ai的响应，拼接到历史会话里面
        assert chatClientResponse
                .chatResponse() != null;
        AssistantMessage assistantMessage = chatClientResponse
                .chatResponse()
                .getResult()
                .getOutput();
        historyMessage.add(assistantMessage);
        // 相当于落库
        chatMemory.put(conversationId, historyMessage);
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
