package com.financeiro.llm.service;

import com.financeiro.llm.dto.ExtratoAtualizadoDTO;
import com.financeiro.llm.model.InsightFinanceiro;
import com.financeiro.llm.repository.InsightRepository;
import com.financeiro.llm.statemachine.StatusInsight;
import org.springframework.stereotype.Component;

@Component
public class InsightFactory {

    private final InsightRepository insightRepository;

    public InsightFactory(InsightRepository insightRepository) {
        this.insightRepository = insightRepository;
    }

    public InsightFinanceiro buscarOuCriar(ExtratoAtualizadoDTO evento) {
        return insightRepository
                .findByUsuarioIdAndMesAndAno(evento.getUsuarioId(), evento.getMes(), evento.getAno())
                .orElseGet(() -> {
                    InsightFinanceiro novo = new InsightFinanceiro();
                    novo.setUsuarioId(evento.getUsuarioId());
                    novo.setMes(evento.getMes());
                    novo.setAno(evento.getAno());
                    novo.setStatus(StatusInsight.RECEBIDO);
                    return insightRepository.save(novo);
                });
    }
}
