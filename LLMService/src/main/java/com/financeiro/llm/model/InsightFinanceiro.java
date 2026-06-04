package com.financeiro.llm.model;

import com.financeiro.llm.statemachine.StatusInsight;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "insights_financeiros",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuarioId", "mes", "ano"}))
public class InsightFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Integer mes;
    private Integer ano;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Enumerated(EnumType.STRING)
    private StatusInsight status = StatusInsight.RECEBIDO;

    private LocalDateTime geradoEm = LocalDateTime.now();
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public StatusInsight getStatus() { return status; }
    public void setStatus(StatusInsight status) {
        this.status = status;
        this.atualizadoEm = LocalDateTime.now();
    }

    public LocalDateTime getGeradoEm() { return geradoEm; }
    public void setGeradoEm(LocalDateTime geradoEm) { this.geradoEm = geradoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
