package com.faknewsdetectorBot.mcpmodule.tools;


import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class PythonTools {

    private final RestTemplate restTemplate;
    private final String PYTHON_URL = "http://localhost:8090";

    public PythonTools(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @McpTool(name = "analyze_news_credibility",
            description = "Analyze if a news text is real (1) or fake (0) with confidence score using AI model")
    public Map<String, Object> analyzeNewsCredibility(
            @McpArg(description = "The news text to analyze") String text) {

        // Appel direct à ton service Python existant
        Map<String, String> request = Map.of("text", text);
        return restTemplate.postForObject(
                PYTHON_URL + "/analyze",
                request,
                Map.class
        );
    }

    @McpTool(name = "check_python_health",
            description = "Check health status of Python fake news detection service")
    public Map<String, Object> checkPythonHealth() {
        return restTemplate.getForObject(PYTHON_URL + "/health", Map.class);
    }
}