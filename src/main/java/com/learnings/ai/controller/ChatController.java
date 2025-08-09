package com.learnings.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.ai.request.ScriptRequest;
import com.learnings.ai.request.ReviewRequest;
import com.learnings.ai.request.TopMoviesRequest;
import com.learnings.ai.response.TopMoviesResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/chat")
    String evaluateScript(@RequestBody ScriptRequest request) {
        return this.chatClient.prompt()
                .system("You are an expert telugu screenwriter evaluator and script consultant with over 20 years of experience in the film industry. Your role is to analyze and provide constructive feedback on screenplays, scripts, and story concepts. You should evaluate:\n\n" +
                        "1. **Story Structure**: Plot development, pacing, and narrative arc\n" +
                        "2. **Character Development**: Character depth, motivation, and growth\n" +
                        "3. **Dialogue Quality**: Natural speech patterns, character voice, and subtext\n" +
                        "4. **Visual Storytelling**: Show vs. tell, cinematic elements, and scene construction\n" +
                        "5. **Genre Conventions**: Adherence to and innovation within genre expectations\n" +
                        "6. **Market Viability**: Commercial potential and audience appeal\n\n" +
                        "Provide specific, actionable feedback with examples. Be honest but constructive. Rate elements on a scale of 1-10 and suggest specific improvements. Always maintain a professional, encouraging tone while being direct about areas that need work.")
                .user(request.getScene())
                .call()
                .content();
    }

    @PostMapping("/review")
    String reviewMovie(@RequestBody ReviewRequest request) {
        String movie = request.getMovieName();
        return this.chatClient.prompt()
                .system("You are a seasoned film critic. Write a concise, insightful review under 200 words for the movie '" + movie + "'. Focus on story, performances, direction, music, and overall impact. Be balanced and specific. End with a one-line verdict.")
                .user("Please review the movie now.")
                .call()
                .content();
    }

    @PostMapping("/top-movies")
    TopMoviesResponse topMovies(@RequestBody TopMoviesRequest request) throws Exception {
        String language = request.getLanguage();
        String json = this.chatClient.prompt()
                .system("You are a knowledgeable film historian. Return a STRICT JSON object with a key 'movies' that is an array of exactly 10 items for the top movies in the '" + language + "' language. Each item must have: title (string), year (number), director (string), genre (string), and oneLineSummary (string). Do not include any extra text, code fences, or commentary.")
                .user("List the top 10 movies now as strict JSON only.")
                .call()
                .content();
        return objectMapper.readValue(json, TopMoviesResponse.class);
    }

    @GetMapping("/test")
    String test() {
        return "Screenwriter Evaluator is working! Your application is running successfully on Java 17 in Docker.\n\n" +
               "To use the screenwriter evaluator, send a POST request to:\n" +
               "http://localhost:8080/chat\n\n" +
               "With JSON body:\n" +
               "{\n" +
               "  \"scene\": \"Your script or story here\"\n" +
               "}\n\n" +
               "The AI will evaluate your work based on:\n" +
               "- Story Structure (1-10 rating)\n" +
               "- Character Development (1-10 rating)\n" +
               "- Dialogue Quality (1-10 rating)\n" +
               "- Visual Storytelling (1-10 rating)\n" +
               "- Genre Conventions (1-10 rating)\n" +
               "- Market Viability (1-10 rating)";
    }
}
