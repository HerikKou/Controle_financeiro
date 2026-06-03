package com.financeiro.llm.controller;

import com.financeiro.llm.model.InsightFinanceiro;
import com.financeiro.llm.repository.InsightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insights")
public class InsightController {

    private final InsightRepository insightRepository;

    public InsightController(InsightRepository insightRepository) {
        this.insightRepository = insightRepository;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<InsightFinanceiro>> buscarInsights(@PathVariable Long usuarioId) {
        List<InsightFinanceiro> insights = insightRepository.findByUsuarioIdOrderByGeradoEmDesc(usuarioId);
        return ResponseEntity.ok(insights);
    }
}
