package com.learnings.ai.request;

public class SpeechRequest {
    private String text;
    private String voice;   // e.g., ALLOY, NOVA, ONYX, SHIMMER, FABLE, ECHO
    private String format;  // e.g., MP3, WAV, FLAC, OPUS, AAC, PCM
    private String model;   // e.g., tts-1 or tts-1-hd
    private Float speed;    // 0.25 to 4.0

    public SpeechRequest() {}

    public SpeechRequest(String text, String voice, String format, String model, Float speed) {
        this.text = text;
        this.voice = voice;
        this.format = format;
        this.model = model;
        this.speed = speed;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Float getSpeed() { return speed; }
    public void setSpeed(Float speed) { this.speed = speed; }
}