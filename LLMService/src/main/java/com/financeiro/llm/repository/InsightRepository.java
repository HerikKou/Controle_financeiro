package com.financeiro.llm.repository;

import com.financeiro.llm.model.InsightFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsightRepository extends JpaRepository<InsightFinanceiro, Long> {

    List<InsightFinanceiro> findByUsuarioIdOrderByGeradoEmDesc(Long usuarioId);

    Optional<InsightFinanceiro> findByUsuarioIdAndMesAndAno(Long usuarioId, Integer mes, Integer ano);
}
