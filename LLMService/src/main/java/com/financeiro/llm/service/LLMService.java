package com.financeiro.llm.service;

import com.financeiro.llm.dto.ExtratoAtualizadoDTO;
import com.financeiro.llm.model.InsightFinanceiro;
import com.financeiro.llm.repository.InsightRepository;
import com.financeiro.llm.statemachine.StatusInsight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.DltHandler;
@Service
public class LLMService {

    private static final Logger log = LoggerFactory.getLogger(LLMService.class);

    private final InsightRepository insightRepository;
    private final ClaudeService claudeService;
    private final InsightFactory insightFactory;
    private final LLMDlqHandler dlqHandler;

    public LLMService(InsightRepository insightRepository, ClaudeService claudeService, InsightFactory insightFactory, LLMDlqHandler dlqHandler) {
        this.insightRepository = insightRepository;
        this.claudeService = claudeService;
        this.insightFactory = insightFactory;
        this.dlqHandler = dlqHandler;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 3000), kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "extrato_atualizado", groupId = "llm-service-group")
    public void consumirExtratoAtualizado(ExtratoAtualizadoDTO evento) {
        if (jaProcessado(evento)) return;

        InsightFinanceiro insight = insightFactory.buscarOuCriar(evento);

        try {
            transicionarPara(insight, StatusInsight.GERANDO);
            String mensagem = claudeService.gerarInsight(evento.getSalario(), evento.getTotalGastoMes(), evento.getMes(), evento.getAno());
            insight.setMensagem(mensagem);
            transicionarPara(insight, StatusInsight.GERADO);
            transicionarPara(insight, StatusInsight.SALVO);
        } catch (Exception e) {
            transicionarPara(insight, StatusInsight.ERRO_GERACAO);
            log.error("[SM] → ERRO_GERACAO → usuarioId={}, erro={}", evento.getUsuarioId(), e.getMessage());
            throw e;
        }
    }

    @DltHandler
    public void escutarDlq(ExtratoAtualizadoDTO evento) {
        dlqHandler.handle(evento);
    }

    private boolean jaProcessado(ExtratoAtualizadoDTO evento) {
        boolean processado = insightRepository.findByUsuarioIdAndMesAndAno(evento.getUsuarioId(), evento.getMes(), evento.getAno()).map(i -> i.getStatus() == StatusInsight.SALVO).orElse(false);
        if (processado) log.info("[IDEMPOTENCIA] Insight já salvo para usuarioId={}, mes={}/{}. Ignorando.", evento.getUsuarioId(), evento.getMes(), evento.getAno());
        return processado;
    }

    private void transicionarPara(InsightFinanceiro insight, StatusInsight novoStatus) {
        insight.setStatus(novoStatus);
        insightRepository.save(insight);
        log.info("[SM] → {} → usuarioId={}", novoStatus, insight.getUsuarioId());
    }
}
