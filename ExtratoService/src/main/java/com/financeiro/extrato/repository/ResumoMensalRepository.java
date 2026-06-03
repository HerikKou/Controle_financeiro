package com.financeiro.extrato.repository;

import com.financeiro.extrato.model.ResumoMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumoMensalRepository extends JpaRepository<ResumoMensal, Long> {
    Optional<ResumoMensal> findByUsuarioIdAndMesAndAno(Long usuarioId, Integer mes, Integer ano);
}
