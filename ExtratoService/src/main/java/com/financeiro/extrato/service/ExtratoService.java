package com.financeiro.extrato.service;

import com.financeiro.extrato.dto.ExtratoAtualizadoDTO;
import com.financeiro.extrato.dto.PagamentoCriadoDTO;
import com.financeiro.extrato.model.ResumoMensal;
import com.financeiro.extrato.repository.ResumoMensalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExtratoService {

    private static final Logger log = LoggerFactory.getLogger(ExtratoService.class);
    private static final String TOPIC_OUT = "extrato_atualizado";

    private final ResumoMensalRepository resumoRepository;
    private final KafkaTemplate<String, ExtratoAtualizadoDTO> kafkaTemplate;

    public ExtratoService(ResumoMensalRepository resumoRepository,
                          KafkaTemplate<String, ExtratoAtualizadoDTO> kafkaTemplate) {
        this.resumoRepository = resumoRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "pagamento_criado", groupId = "extrato-service-group")
    public void consumirPagamentoCriado(PagamentoCriadoDTO evento) {
        log.info("Evento recebido: pagamentoId={}, usuarioId={}, valor={}",
                evento.getPagamentoId(), evento.getUsuarioId(), evento.getValor());

        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        Optional<ResumoMensal> optResumo = resumoRepository
                .findByUsuarioIdAndMesAndAno(evento.getUsuarioId(), mes, ano);

        ResumoMensal resumo = optResumo.orElseGet(() -> {
            ResumoMensal novo = new ResumoMensal();
            novo.setUsuarioId(evento.getUsuarioId());
            novo.setMes(mes);
            novo.setAno(ano);
            novo.setSalario(BigDecimal.valueOf(5000)); // TODO: buscar salário do usuário
            novo.setTotalGastoMes(BigDecimal.ZERO);
            return novo;
        });

        resumo.setTotalGastoMes(resumo.getTotalGastoMes().add(evento.getValor()));
        ResumoMensal salvo = resumoRepository.save(resumo);
        log.info("Resumo atualizado: usuarioId={}, totalGasto={}", salvo.getUsuarioId(), salvo.getTotalGastoMes());

        ExtratoAtualizadoDTO saida = new ExtratoAtualizadoDTO(
                salvo.getId(), salvo.getUsuarioId(), salvo.getSalario(),
                salvo.getTotalGastoMes(), salvo.getMes(), salvo.getAno()
        );

        kafkaTemplate.send(TOPIC_OUT, String.valueOf(salvo.getUsuarioId()), saida);
        log.info("Evento ExtratoAtualizado publicado no tópico {}", TOPIC_OUT);
    }
}
