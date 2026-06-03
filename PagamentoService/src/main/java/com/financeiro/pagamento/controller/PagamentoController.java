package com.financeiro.pagamento.controller;

import com.financeiro.pagamento.dto.CriarPagamentoDTO;
import com.financeiro.pagamento.model.Pagamento;
import com.financeiro.pagamento.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<Pagamento> criarPagamento(@RequestBody CriarPagamentoDTO dto) {
        Pagamento pagamento = pagamentoService.registrarPagamento(dto);
        return ResponseEntity.ok(pagamento);
    }
}
