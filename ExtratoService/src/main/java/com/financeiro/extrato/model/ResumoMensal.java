package com.financeiro.extrato.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "resumo_mensal")
public class ResumoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private BigDecimal salario;

    private BigDecimal totalGastoMes = BigDecimal.ZERO;

    private Integer mes;

    private Integer ano;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
