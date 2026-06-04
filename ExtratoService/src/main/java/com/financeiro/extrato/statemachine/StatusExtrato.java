package com.financeiro.extrato.statemachine;

/**
 * Ciclo de vida do processamento de um evento no ExtratoService.
 *
 * RECEBIDO ──► PROCESSANDO ──► CONSOLIDADO ──► PUBLICADO
 *                  │                               │
 *                  └──────────► ERRO_PROCESSAMENTO─┘
 *                                      │
 *                                      └──► DLQ (após retries esgotados)
 *
 * - RECEBIDO:           evento pagamento_criado chegou do Kafka
 * - PROCESSANDO:        buscando/atualizando resumo mensal no banco
 * - CONSOLIDADO:        resumo salvo com sucesso
 * - PUBLICADO:          evento extrato_atualizado publicado no Kafka
 * - ERRO_PROCESSAMENTO: falha em alguma etapa (banco ou Kafka)
 */
public enum StatusExtrato {
    RECEBIDO,
    PROCESSANDO,
    CONSOLIDADO,
    PUBLICADO,
    ERRO_PROCESSAMENTO
}
