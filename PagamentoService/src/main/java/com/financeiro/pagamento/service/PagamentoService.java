package com.financeiro.pagamento.service;

import com.financeiro.pagamento.dto.CriarPagamentoDTO;
import com.financeiro.pagamento.dto.PagamentoCriadoDTO;
import com.financeiro.pagamento.model.Pagamento;
import com.financeiro.pagamento.repository.PagamentoRepository;
import com.financeiro.pagamento.statemachine.StatusPagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);
    private static final String TOPIC = "pagamento_criado";

    private final PagamentoRepository pagamentoRepository;
    private final KafkaTemplate<String, PagamentoCriadoDTO> kafkaTemplate;

    public PagamentoService(PagamentoRepository pagamentoRepository,
                            KafkaTemplate<String, PagamentoCriadoDTO> kafkaTemplate) {
        this.pagamentoRepository = pagamentoRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Pagamento registrarPagamento(CriarPagamentoDTO dto) {
        // Estado inicial: CRIADO
        Pagamento pagamento = new Pagamento();
        pagamento.setUsuarioId(dto.getUsuarioId());
        pagamento.setValor(dto.getValor());
        pagamento.setDescricao(dto.getDescricao());
        pagamento.setStatus(StatusPagamento.CRIADO);

        Pagamento salvo = pagamentoRepository.save(pagamento);
        log.info("[SM] CRIADO → id={}, usuarioId={}, valor={}", salvo.getId(), salvo.getUsuarioId(), salvo.getValor());

        try {
            PagamentoCriadoDTO evento = new PagamentoCriadoDTO(salvo.getId(), salvo.getUsuarioId(), salvo.getValor());
            kafkaTemplate.send(TOPIC, String.valueOf(salvo.getUsuarioId()), evento);

            // Transição: CRIADO → PUBLICADO
            salvo.setStatus(StatusPagamento.PUBLICADO);
            pagamentoRepository.save(salvo);
            log.info("[SM] CRIADO → PUBLICADO → id={}, tópico={}", salvo.getId(), TOPIC);

        } catch (Exception e) {
            // Transição: CRIADO → ERRO_PUBLICACAO
            salvo.setStatus(StatusPagamento.ERRO_PUBLICACAO);
            pagamentoRepository.save(salvo);
            log.error("[SM] CRIADO → ERRO_PUBLICACAO → id={}, erro={}", salvo.getId(), e.getMessage());
        }

        return salvo;
    }
}
