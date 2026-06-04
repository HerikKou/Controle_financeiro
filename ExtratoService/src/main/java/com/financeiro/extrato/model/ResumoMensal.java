package com.financeiro.extrato.model;

import com.financeiro.extrato.statemachine.StatusExtrato;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Long ultimoPagamentoIdProcessado;

    @Enumerated(EnumType.STRING)
    private StatusExtrato status = StatusExtrato.RECEBIDO;

    private LocalDateTime atualizadoEm = LocalDateTime.now();

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

    public Long getUltimoPagamentoIdProcessado() { return ultimoPagamentoIdProcessado; }
    public void setUltimoPagamentoIdProcessado(Long ultimoPagamentoIdProcessado) {
        this.ultimoPagamentoIdProcessado = ultimoPagamentoIdProcessado;
    }

    public StatusExtrato getStatus() { return status; }
    public void setStatus(StatusExtrato status) {
        this.status = status;
        this.atualizadoEm = LocalDateTime.now();
    }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
