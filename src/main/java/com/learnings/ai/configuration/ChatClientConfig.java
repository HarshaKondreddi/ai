package com.learnings.ai.configuration;

import com.learnings.ai.advisor.SimpleLogAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public SimpleLogAdvisor simpleLogAdvisor() {
        return new SimpleLogAdvisor();
    }

    @Bean
    public ChatClient.Builder chatClientBuilder(OpenAiChatModel chatModel, SimpleLogAdvisor simpleLogAdvisor, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        simpleLogAdvisor,
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                );
    }

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}
