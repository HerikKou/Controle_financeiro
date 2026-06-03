package com.financeiro.pagamento.dto;

import java.math.BigDecimal;

public class CriarPagamentoDTO {

    private Long usuarioId;
    private BigDecimal valor;
    private String descricao;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
