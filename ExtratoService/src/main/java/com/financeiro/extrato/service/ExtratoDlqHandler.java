package com.financeiro.extrato.service;

import com.financeiro.extrato.dto.PagamentoCriadoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtratoDlqHandler {

    private static final Logger log = LoggerFactory.getLogger(ExtratoDlqHandler.class);

    public void handle(PagamentoCriadoDTO evento) {
        log.error("[DLQ] Evento morto. pagamentoId={} | usuarioId={} | valor={}",
                evento.getPagamentoId(), evento.getUsuarioId(), evento.getValor());
    }
}
