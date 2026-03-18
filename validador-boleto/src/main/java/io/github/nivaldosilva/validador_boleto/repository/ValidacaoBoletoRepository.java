package io.github.nivaldosilva.validador_boleto.repository;

import io.github.nivaldosilva.validador_boleto.entity.ValidacaoBoleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidacaoBoletoRepository extends JpaRepository<ValidacaoBoleto, Long> {

    boolean existsByCodigoBarras(String codigoBarras);
}