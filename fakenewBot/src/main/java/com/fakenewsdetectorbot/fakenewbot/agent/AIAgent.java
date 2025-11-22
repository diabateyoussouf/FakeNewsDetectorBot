package com.fakenewsdetectorbot.fakenewbot.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AIAgent {
    private ChatClient chatClient;

    public AIAgent(ChatClient.Builder builder,
                   ChatMemory memory, ToolCallbackProvider tools) {

        // Debug: Afficher les tools disponibles
        Arrays.stream(tools.getToolCallbacks()).forEach(toolCallback -> {
            System.out.println(" Tool disponible: " + toolCallback.getToolDefinition().name());
            System.out.println("Description: " + toolCallback.getToolDefinition().description());
            System.out.println("----------------------");
        });

        this.chatClient = builder
                .defaultSystem("""
                    Vous êtes un détecteur de fake news. Répondez TOUJOURS en 3 parties :
                    
                    1. 🎯 RÉSULTAT (REAL/FAKE/INCERTAIN) + score confiance
                    2. 📝 Explication courte (2-3 lignes max)
                    3. 🔍 Vérification recommandée (1-2 points)
                    
                    RÈGLES :
                    - Réponse MAX 500 caractères
                    - Direct, clair, concis
                    - Pas d'analyse académique
                    - Utilisez des émojis pour la lisibilité
                    
                    EXEMPLE :
                    "🔍 RÉSULTAT: POTENTIELLEMENT FAKE 🚨
                    📝 Langage sensationnaliste typique des fake news
                    🔍 Vérifiez: Sources officielles manquantes"
                """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .defaultToolCallbacks(tools)
                .build();
    }

    public String askAgent(String query) {
        return chatClient.prompt()
                .user(query)
                .call().content();
    }

    // Méthode spécialisée pour l'analyse de fake news
    public String analyzeNews(String newsText) {
        return chatClient.prompt()
                .user("Analyse la crédibilité de cette information : " + newsText)
                .call().content();
    }

    // Méthode pour comparer des sources
    public String compareSources(String[] sources, String[] labels) {
        String prompt = String.format(
                "Compare la crédibilité de ces %d sources d'information. Sources: %s",
                sources.length, Arrays.toString(labels)
        );

        return chatClient.prompt()
                .user(prompt)
                .call().content();
    }
}