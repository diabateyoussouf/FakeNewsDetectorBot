package com.fakenewsdetectorbot.fakenewbot.controller;

import com.fakenewsdetectorbot.fakenewbot.agent.AIAgent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final AIAgent aiAgent;

    public ChatController(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    // Endpoint général de chat
    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam(name = "query") String query) {
        return aiAgent.askAgent(query);
    }

    // Endpoint spécialisé pour l'analyse de fake news
    @PostMapping(value = "/analyze-news", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> analyzeNews(@RequestBody Map<String, String> request) {
        try {
            String newsText = request.get("text");

            if (newsText == null || newsText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Le paramètre 'text' est requis"
                ));
            }

            String analysis = aiAgent.analyzeNews(newsText);

            return ResponseEntity.ok(Map.of(
                    "analysis", analysis,
                    "timestamp", System.currentTimeMillis(),
                    "status", "success"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Erreur lors de l'analyse: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }


    // Endpoint pour analyser un message Telegram
    @PostMapping(value = "/telegram/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> analyzeTelegramMessage(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String chatId = request.get("chatId");

            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Le paramètre 'message' est requis"
                ));
            }

            String response = aiAgent.analyzeNews(message);

            return ResponseEntity.ok(Map.of(
                    "chat_id", chatId != null ? chatId : "unknown",
                    "response", response,
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Erreur de traitement: " + e.getMessage()
            ));
        }
    }

    // Endpoint de santé
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "Fake News Detector AI Agent",
                "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}