package com.financeiro.extrato.service;

import com.financeiro.extrato.dto.ExtratoAtualizadoDTO;
import com.financeiro.extrato.dto.PagamentoCriadoDTO;
import com.financeiro.extrato.model.ResumoMensal;
import com.financeiro.extrato.repository.ResumoMensalRepository;
import com.financeiro.extrato.statemachine.StatusExtrato;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExtratoService {

    private static final Logger log = LoggerFactory.getLogger(ExtratoService.class);

    private final ResumoMensalRepository resumoRepository;
    private final ExtratoPublisher extratoPublisher;
    private final ExtratoDlqHandler dlqHandler;

    public ExtratoService(ResumoMensalRepository resumoRepository, ExtratoPublisher extratoPublisher, ExtratoDlqHandler dlqHandler) {
        this.resumoRepository = resumoRepository;
        this.extratoPublisher = extratoPublisher;
        this.dlqHandler = dlqHandler;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 3000), kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "pagamento_criado", groupId = "extrato-service-group")
    public void consumirPagamentoCriado(PagamentoCriadoDTO evento) {
        if (jaProcessado(evento)) return;

        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        ResumoMensal resumo = buscarOuCriarResumo(evento, mes, ano);

        try {
            transicionarPara(resumo, StatusExtrato.PROCESSANDO);
            resumo.setTotalGastoMes(resumo.getTotalGastoMes().add(evento.getValor()));
            resumo.setUltimoPagamentoIdProcessado(evento.getPagamentoId());
            transicionarPara(resumo, StatusExtrato.CONSOLIDADO);
            extratoPublisher.publicar(resumo);
            transicionarPara(resumo, StatusExtrato.PUBLICADO);
        } catch (Exception e) {
            transicionarPara(resumo, StatusExtrato.ERRO_PROCESSAMENTO);
            log.error("[SM] → ERRO_PROCESSAMENTO → usuarioId={}, erro={}", resumo.getUsuarioId(), e.getMessage());
            throw e;
        }
    }

    @KafkaListener(topics = "dlq", groupId = "extrato-service-dlq-group")
    public void escutarDlq(PagamentoCriadoDTO evento) {
        dlqHandler.handle(evento);
    }

    private boolean jaProcessado(PagamentoCriadoDTO evento) {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();
        boolean processado = resumoRepository.existsByUsuarioIdAndMesAndAnoAndPagamentoIdProcessado(evento.getUsuarioId(), mes, ano, evento.getPagamentoId());
        if (processado) log.info("[IDEMPOTENCIA] pagamentoId={} já processado. Ignorando.", evento.getPagamentoId());
        return processado;
    }

    private ResumoMensal buscarOuCriarResumo(PagamentoCriadoDTO evento, int mes, int ano) {
        return resumoRepository.findByUsuarioIdAndMesAndAno(evento.getUsuarioId(), mes, ano).orElseGet(() -> {
            ResumoMensal novo = new ResumoMensal();
            novo.setUsuarioId(evento.getUsuarioId());
            novo.setMes(mes);
            novo.setAno(ano);
            novo.setSalario(BigDecimal.valueOf(5000));
            novo.setTotalGastoMes(BigDecimal.ZERO);
            novo.setStatus(StatusExtrato.RECEBIDO);
            return resumoRepository.save(novo);
        });
    }

    private void transicionarPara(ResumoMensal resumo, StatusExtrato novoStatus) {
        resumo.setStatus(novoStatus);
        resumoRepository.save(resumo);
        log.info("[SM] → {} → usuarioId={}", novoStatus, resumo.getUsuarioId());
    }
}
