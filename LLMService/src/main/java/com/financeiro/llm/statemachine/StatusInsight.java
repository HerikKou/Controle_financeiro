package com.financeiro.llm.statemachine;

/**
 * Ciclo de vida do processamento de um insight no LLMService.
 *
 * RECEBIDO ──► GERANDO ──► GERADO ──► SALVO
 *                │
 *                └──► ERRO_GERACAO (falha na Claude API → retry → DLQ)
 *
 * - RECEBIDO:     evento extrato_atualizado chegou do Kafka
 * - GERANDO:      chamando a Claude API
 * - GERADO:       resposta recebida com sucesso
 * - SALVO:        insight persistido no banco
 * - ERRO_GERACAO: falha na Claude API após todos os retries
 */
public enum StatusInsight {
    RECEBIDO,
    GERANDO,
    GERADO,
    SALVO,
    ERRO_GERACAO
}
