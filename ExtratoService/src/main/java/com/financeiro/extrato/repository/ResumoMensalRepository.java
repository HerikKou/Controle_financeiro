package com.financeiro.extrato.repository;

import com.financeiro.extrato.model.ResumoMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumoMensalRepository extends JpaRepository<ResumoMensal, Long> {

    Optional<ResumoMensal> findByUsuarioIdAndMesAndAno(Long usuarioId, Integer mes, Integer ano);

    @Query("SELECT COUNT(r) > 0 FROM ResumoMensal r WHERE r.usuarioId = :usuarioId AND r.mes = :mes AND r.ano = :ano AND r.ultimoPagamentoIdProcessado = :pagamentoId")
    boolean existsByUsuarioIdAndMesAndAnoAndPagamentoIdProcessado(
            @Param("usuarioId") Long usuarioId,
            @Param("mes") Integer mes,
            @Param("ano") Integer ano,
            @Param("pagamentoId") Long pagamentoId);
}
