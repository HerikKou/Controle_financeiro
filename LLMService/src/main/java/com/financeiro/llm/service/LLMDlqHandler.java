package com.financeiro.llm.service;

import com.financeiro.llm.dto.ExtratoAtualizadoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LLMDlqHandler {

    private static final Logger log = LoggerFactory.getLogger(LLMDlqHandler.class);

    public void handle(ExtratoAtualizadoDTO evento) {
        log.error("[DLQ] Evento morto. usuarioId={} | mes={}/{} | totalGasto={}",
                evento.getUsuarioId(), evento.getMes(), evento.getAno(), evento.getTotalGastoMes());
    }
}
