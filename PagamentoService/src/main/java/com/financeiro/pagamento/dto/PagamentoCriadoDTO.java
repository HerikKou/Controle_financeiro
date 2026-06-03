package com.financeiro.pagamento.dto;

import java.math.BigDecimal;

public class PagamentoCriadoDTO {

    private Long pagamentoId;
    private Long usuarioId;
    private BigDecimal valor;

    public PagamentoCriadoDTO() {}

    public PagamentoCriadoDTO(Long pagamentoId, Long usuarioId, BigDecimal valor) {
        this.pagamentoId = pagamentoId;
        this.usuarioId = usuarioId;
        this.valor = valor;
    }

    public Long getPagamentoId() { return pagamentoId; }
    public void setPagamentoId(Long pagamentoId) { this.pagamentoId = pagamentoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
