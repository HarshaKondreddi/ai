package com.learnings.ai.controller;

import com.learnings.ai.request.SpeechRequest;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpeechController {

    private final OpenAiAudioSpeechModel speechModel;

    @Autowired
    public SpeechController(OpenAiAudioSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    @PostMapping(value = "/speech/tts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> tts(@RequestBody SpeechRequest req) {
        String text = req.getText();
        OpenAiAudioApi.SpeechRequest.Voice voice = parseVoice(req.getVoice());
        OpenAiAudioApi.SpeechRequest.AudioResponseFormat fmt = parseFormat(req.getFormat());
        String model = (req.getModel() == null || req.getModel().isBlank()) ? "tts-1" : req.getModel();
        float speed = req.getSpeed() == null ? 1.0f : req.getSpeed();

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model(model)
                .voice(voice)
                .responseFormat(fmt)
                .speed(speed)
                .build();

        SpeechPrompt prompt = new SpeechPrompt(text, options);
        SpeechResponse response = speechModel.call(prompt);
        byte[] audio = response.getResult().getOutput();

        MediaType mediaType = mediaTypeFor(fmt);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=tts." + fileExt(fmt))
                .contentType(mediaType)
                .body(audio);
    }

    private static OpenAiAudioApi.SpeechRequest.Voice parseVoice(String v) {
        if (v == null || v.isBlank()) return OpenAiAudioApi.SpeechRequest.Voice.ALLOY;
        try { return OpenAiAudioApi.SpeechRequest.Voice.valueOf(v.toUpperCase()); }
        catch (Exception e) { return OpenAiAudioApi.SpeechRequest.Voice.ALLOY; }
    }

    private static OpenAiAudioApi.SpeechRequest.AudioResponseFormat parseFormat(String f) {
        if (f == null || f.isBlank()) return OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3;
        try { return OpenAiAudioApi.SpeechRequest.AudioResponseFormat.valueOf(f.toUpperCase()); }
        catch (Exception e) { return OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3; }
    }

    private static MediaType mediaTypeFor(OpenAiAudioApi.SpeechRequest.AudioResponseFormat fmt) {
        return switch (fmt) {
            case WAV -> MediaType.parseMediaType("audio/wav");
            case FLAC -> MediaType.parseMediaType("audio/flac");
            case OPUS -> MediaType.parseMediaType("audio/opus");
            case AAC -> MediaType.parseMediaType("audio/aac");
            case PCM -> MediaType.parseMediaType("audio/L16");
            default -> MediaType.parseMediaType("audio/mpeg"); // MP3
        };
    }

    private static String fileExt(OpenAiAudioApi.SpeechRequest.AudioResponseFormat fmt) {
        return switch (fmt) {
            case WAV -> "wav";
            case FLAC -> "flac";
            case OPUS -> "opus";
            case AAC -> "aac";
            case PCM -> "pcm";
            default -> "mp3";
        };
    }
}