package com.learnings.ai.controller;

import com.learnings.ai.request.ImageRequest;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ImageController {

    private final ImageModel imageModel;

    @Autowired
    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @PostMapping(value = "/images/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> generate(@RequestBody ImageRequest req) {
        int width = req.getWidth() == null ? 1024 : req.getWidth();
        int height = req.getHeight() == null ? 1024 : req.getHeight();
        int n = req.getN() == null ? 1 : req.getN();
        String quality = req.getQuality() == null ? "standard" : req.getQuality();

        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .width(width)
                .height(height)
                .N(n)
                .quality(quality)
                .build();

        ImagePrompt prompt = new ImagePrompt(req.getPrompt(), options);
        ImageResponse response = imageModel.call(prompt);

        List<String> urls = response.getResults().stream()
                .map(r -> r.getOutput().getUrl())
                .toList();

        Map<String, Object> out = new HashMap<>();
        out.put("urls", urls);
        out.put("count", urls.size());
        return out;
    }
}