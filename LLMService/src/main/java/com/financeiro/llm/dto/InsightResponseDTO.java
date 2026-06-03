package com.financeiro.llm.dto;

public class InsightResponseDTO {

    private Long usuarioId;
    private Integer mes;
    private Integer ano;
    private String mensagem;

    public InsightResponseDTO() {}

    public InsightResponseDTO(Long usuarioId, Integer mes, Integer ano, String mensagem) {
        this.usuarioId = usuarioId;
        this.mes = mes;
        this.ano = ano;
        this.mensagem = mensagem;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
