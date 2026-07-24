package com.agentseek.model.client;

import com.agentseek.model.dto.ChatRequest;
import com.agentseek.model.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek 客户端实现
 */
@Slf4j
public class DeepSeekClient implements LLMClient {

    private final String baseUrl;
    private final String apiKey;
    private final String modelName;
    private final RestTemplate restTemplate;

    public DeepSeekClient(String baseUrl, String apiKey, String modelName) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        String url = baseUrl + "chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", request.getModel() != null ? request.getModel() : modelName);
        body.put("messages", request.getMessages());
        if (request.getTemperature() != null) body.put("temperature", request.getTemperature());
        if (request.getMaxTokens() != null) body.put("max_tokens", request.getMaxTokens());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            log.error("DeepSeek API call failed: {}", e.getMessage());
            return ChatResponse.builder()
                    .content("调用 DeepSeek 失败: " + e.getMessage())
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private ChatResponse parseResponse(Map<String, Object> body) {
        if (body == null) return ChatResponse.builder().content("").build();

        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        String content = "";
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message != null) {
                content = (String) message.get("content");
            }
        }

        Map<String, Object> usage = (Map<String, Object>) body.get("usage");
        Integer promptTokens = usage != null ? (Integer) usage.get("prompt_tokens") : 0;
        Integer completionTokens = usage != null ? (Integer) usage.get("completion_tokens") : 0;

        return ChatResponse.builder()
                .id((String) body.get("id"))
                .model((String) body.get("model"))
                .content(content)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(promptTokens + completionTokens)
                .build();
    }

    @Override
    public String getProvider() {
        return "deepseek";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
