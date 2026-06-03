package com.financeiro.llm.service;

import com.financeiro.llm.config.ClaudeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);

    private final ClaudeConfig claudeConfig;
    private final RestTemplate restTemplate;

    public ClaudeService(ClaudeConfig claudeConfig, RestTemplate restTemplate) {
        this.claudeConfig = claudeConfig;
        this.restTemplate = restTemplate;
    }

    public String gerarInsight(BigDecimal salario, BigDecimal totalGasto, Integer mes, Integer ano) {
        String prompt = String.format(
                "Você é um consultor financeiro. Analise os dados abaixo e gere uma recomendação financeira personalizada em português:\n\n" +
                "- Salário mensal: R$ %.2f\n" +
                "- Total gasto no mês %d/%d: R$ %.2f\n\n" +
                "Calcule o percentual gasto, quanto resta e faça uma recomendação de economia. Seja direto e amigável.",
                salario, mes, ano, totalGasto
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", claudeConfig.getApiKey());
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> body = Map.of(
                "model", claudeConfig.getModel(),
                "max_tokens", 500,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(claudeConfig.getApiUrl(), request, Map.class);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            log.error("Erro ao chamar Claude API: {}", e.getMessage());
            return String.format("Você gastou R$ %.2f de R$ %.2f neste mês. Restam R$ %.2f.",
                    totalGasto, salario, salario.subtract(totalGasto));
        }
    }
}
