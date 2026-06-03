package com.financeiro.extrato.controller;

import com.financeiro.extrato.model.ResumoMensal;
import com.financeiro.extrato.repository.ResumoMensalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/extrato")
public class ExtratoController {

    private final ResumoMensalRepository resumoRepository;

    public ExtratoController(ResumoMensalRepository resumoRepository) {
        this.resumoRepository = resumoRepository;
    }

    @GetMapping("/{usuarioId}/{mes}/{ano}")
    public ResponseEntity<ResumoMensal> buscarResumo(
            @PathVariable Long usuarioId,
            @PathVariable Integer mes,
            @PathVariable Integer ano) {
        Optional<ResumoMensal> resumo = resumoRepository.findByUsuarioIdAndMesAndAno(usuarioId, mes, ano);
        return resumo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
