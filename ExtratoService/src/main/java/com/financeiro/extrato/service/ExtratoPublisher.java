package com.financeiro.extrato.service;

import com.financeiro.extrato.dto.ExtratoAtualizadoDTO;
import com.financeiro.extrato.model.ResumoMensal;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExtratoPublisher {

    private final KafkaTemplate<String, ExtratoAtualizadoDTO> kafkaTemplate;

    public ExtratoPublisher(KafkaTemplate<String, ExtratoAtualizadoDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(ResumoMensal resumo) {
        ExtratoAtualizadoDTO evento = new ExtratoAtualizadoDTO(
                resumo.getId(), resumo.getUsuarioId(), resumo.getSalario(),
                resumo.getTotalGastoMes(), resumo.getMes(), resumo.getAno()
        );
        kafkaTemplate.send("extrato_atualizado", String.valueOf(resumo.getUsuarioId()), evento);
    }
}
