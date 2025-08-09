package com.learnings.ai.request;

public class ImageRequest {
    private String prompt;
    private Integer width;
    private Integer height;
    private Integer n;
    private String quality; // e.g., "standard" or "hd"

    public ImageRequest() {}

    public ImageRequest(String prompt, Integer width, Integer height, Integer n, String quality) {
        this.prompt = prompt;
        this.width = width;
        this.height = height;
        this.n = n;
        this.quality = quality;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}