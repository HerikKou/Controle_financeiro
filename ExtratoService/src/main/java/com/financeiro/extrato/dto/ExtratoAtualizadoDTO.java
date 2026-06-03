package com.financeiro.extrato.dto;

import java.math.BigDecimal;

public class ExtratoAtualizadoDTO {

    private Long extratoId;
    private Long usuarioId;
    private BigDecimal salario;
    private BigDecimal totalGastoMes;
    private Integer mes;
    private Integer ano;

    public ExtratoAtualizadoDTO() {}

    public ExtratoAtualizadoDTO(Long extratoId, Long usuarioId, BigDecimal salario,
                                 BigDecimal totalGastoMes, Integer mes, Integer ano) {
        this.extratoId = extratoId;
        this.usuarioId = usuarioId;
        this.salario = salario;
        this.totalGastoMes = totalGastoMes;
        this.mes = mes;
        this.ano = ano;
    }

    public Long getExtratoId() { return extratoId; }
    public void setExtratoId(Long extratoId) { this.extratoId = extratoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public BigDecimal getTotalGastoMes() { return totalGastoMes; }
    public void setTotalGastoMes(BigDecimal totalGastoMes) { this.totalGastoMes = totalGastoMes; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
}
