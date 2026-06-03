package com.financeiro.extrato.dto;

import java.math.BigDecimal;

public class PagamentoCriadoDTO {

    private Long pagamentoId;
    private Long usuarioId;
    private BigDecimal valor;

    public Long getPagamentoId() { return pagamentoId; }
    public void setPagamentoId(Long pagamentoId) { this.pagamentoId = pagamentoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
