package com.learnings.ai.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @PostMapping("/embeddings")
    public Map<String, Object> embed(@RequestBody Map<String, String> body) {
        String text = body.getOrDefault("text", "");
        float[] vector = embeddingModel.embed(text);
        return Map.of("embedding", vector, "dimensions", vector.length);
    }

    @PostMapping("/embeddings/batch")
    public Map<String, Object> embedBatch(@RequestBody Map<String, List<String>> body) {
        List<String> texts = body.getOrDefault("texts", List.of());
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        return Map.of("results", response);
    }
}