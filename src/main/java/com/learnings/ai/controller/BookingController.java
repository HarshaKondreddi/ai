package com.learnings.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.ai.response.MovieInfo;
import com.learnings.ai.service.MovieBookingService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final ChatClient chatClient;
    private final MovieBookingService bookingTools;
    private final ChatMemory chatMemory;
    private final VectorStore vectorStore;

    private final ObjectMapper mapper = new ObjectMapper();

    public BookingController(ChatClient.Builder builder, MovieBookingService bookingTools, ChatMemory chatMemory, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.bookingTools = bookingTools;
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
    }

    // Expose tools through Chat and persist conversation to ChatMemory (Cassandra)
    @PostMapping("/chat")
    public String chatWithBookingTools(@RequestParam(name = "conversationId", required = false, defaultValue = "booking") String conversationId,
                                       @RequestBody String userRequest) {
        return chatClient
                .prompt()
                .advisors(spec -> spec.param(CONVERSATION_ID, conversationId))
                .system("You are a helpful movie ticket assistant. Use the provided booking tools to search, show seats, book, or cancel. Always return concise JSON-like answers when appropriate.")
                .tools(bookingTools)
                .user(userRequest)
                .call()
                .content();
    }

    // History and clear endpoints backed by ChatMemory
    @GetMapping("/{conversationId}/history")
    public List<Message> history(@PathVariable String conversationId) {
        return chatMemory.get(conversationId);
    }

    @DeleteMapping("/{conversationId}/history")
    public String clear(@PathVariable String conversationId) {
        chatMemory.clear(conversationId);
        return "Cleared conversation: " + conversationId;
    }

    // Direct REST wrappers over the dummy service (optional)
    @GetMapping("/shows")
    public List<Map<String, Object>> shows(@RequestParam String city, @RequestParam String date) {
        return bookingTools.searchShows(city, date);
    }

    @GetMapping("/seats/{showId}")
    public Map<String, Object> seats(@PathVariable String showId) {
        return bookingTools.getSeatLayout(showId);
    }

    @PostMapping("/book")
    public Map<String, Object> book(@RequestParam String showId,
                                    @RequestParam List<String> seats,
                                    @RequestParam String user) {
        return bookingTools.bookSeats(showId, seats, user);
    }

    @PostMapping("/cancel/{bookingId}")
    public Map<String, Object> cancel(@PathVariable String bookingId) {
        return bookingTools.cancelBooking(bookingId);
    }

    // Seed vector store with synthetic movie shows
    @PostMapping("/seed")
    public String seed(@RequestParam(name = "count", required = false, defaultValue = "1000") int count) {
        bookingTools.seedMoviesIntoVectorStore(count);
        return "Seeding requested: " + count + " documents";
    }
}

