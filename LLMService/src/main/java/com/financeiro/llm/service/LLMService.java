package com.financeiro.llm.service;

import com.financeiro.llm.dto.ExtratoAtualizadoDTO;
import com.financeiro.llm.model.InsightFinanceiro;
import com.financeiro.llm.repository.InsightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LLMService {

    private static final Logger log = LoggerFactory.getLogger(LLMService.class);

    private final InsightRepository insightRepository;
    private final ClaudeService claudeService;

    public LLMService(InsightRepository insightRepository, ClaudeService claudeService) {
        this.insightRepository = insightRepository;
        this.claudeService = claudeService;
    }

    @KafkaListener(topics = "extrato_atualizado", groupId = "llm-service-group")
    public void consumirExtratoAtualizado(ExtratoAtualizadoDTO evento) {
        log.info("Evento recebido: usuarioId={}, salario={}, totalGasto={}, mes={}/{}",
                evento.getUsuarioId(), evento.getSalario(), evento.getTotalGastoMes(),
                evento.getMes(), evento.getAno());

        String mensagem = claudeService.gerarInsight(
                evento.getSalario(),
                evento.getTotalGastoMes(),
                evento.getMes(),
                evento.getAno()
        );

        InsightFinanceiro insight = new InsightFinanceiro();
        insight.setUsuarioId(evento.getUsuarioId());
        insight.setMes(evento.getMes());
        insight.setAno(evento.getAno());
        insight.setMensagem(mensagem);

        insightRepository.save(insight);
        log.info("Insight salvo para usuarioId={}", evento.getUsuarioId());
    }
}
