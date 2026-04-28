package com.abc.config;

import com.abc.tools.TimeTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider weatherTools(TimeTools timeTools) {
        return MethodToolCallbackProvider.builder().toolObjects(timeTools).build();
    }

}