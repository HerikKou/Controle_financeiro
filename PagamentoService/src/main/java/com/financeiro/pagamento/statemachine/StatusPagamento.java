package com.financeiro.pagamento.statemachine;

/**
 * Ciclo de vida de um Pagamento.
 *
 * CRIADO ──► PUBLICADO ──► ERRO_PUBLICACAO
 *
 * - CRIADO:           pagamento persistido no banco, evento ainda não enviado ao Kafka
 * - PUBLICADO:        evento pagamento_criado enviado com sucesso ao Kafka
 * - ERRO_PUBLICACAO:  falha ao publicar no Kafka após todas as tentativas
 */
public enum StatusPagamento {
    CRIADO,
    PUBLICADO,
    ERRO_PUBLICACAO
}
