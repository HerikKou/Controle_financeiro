package com.financeiro.extrato.statemachine;

public enum StatusExtrato {
    RECEBIDO,
    PROCESSANDO,
    CONSOLIDADO,
    PUBLICADO,
    ERRO_PROCESSAMENTO
}
