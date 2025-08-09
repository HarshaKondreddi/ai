package com.learnings.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/memory/chat")
public class ChatMemoryController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatMemoryController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder.build();
        this.chatMemory = chatMemory;
    }

    // Single conversationId chat endpoint that persists history in Cassandra
    @PostMapping("/{conversationId}")
    public String chat(@PathVariable String conversationId, @RequestBody String userText) {
        return chatClient
                .prompt()
                .advisors(spec -> spec.param(CONVERSATION_ID, conversationId))
                .user(userText)
                .call()
                .content();
    }

    // Fetch conversation messages
    @GetMapping("/{conversationId}/history")
    public List<Message> history(@PathVariable String conversationId) {
        return chatMemory.get(conversationId);
    }

    // Clear conversation
    @DeleteMapping("/{conversationId}/history")
    public String clear(@PathVariable String conversationId) {
        chatMemory.clear(conversationId);
        return "Cleared conversation: " + conversationId;
    }
}