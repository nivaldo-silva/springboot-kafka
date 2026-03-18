package io.github.nivaldosilva.api_boleto.repository;

import io.github.nivaldosilva.api_boleto.entity.Boleto;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    Optional<Boleto> findByCodigoBarras(String codigoBarras);

    boolean existsByCodigoBarras(String codigoBarras);

    @Query("""
            SELECT b FROM Boleto b
            WHERE (:situacao   IS NULL OR b.situacao       = :situacao)
              AND (:dataInicio IS NULL OR b.dataVencimento >= :dataInicio)
              AND (:dataFim    IS NULL OR b.dataVencimento <= :dataFim)
            """)
    Page<Boleto> listarComFiltros(
            @Param("situacao")   StatusBoleto situacao,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim")    LocalDate dataFim,
            Pageable pageable
    );
}