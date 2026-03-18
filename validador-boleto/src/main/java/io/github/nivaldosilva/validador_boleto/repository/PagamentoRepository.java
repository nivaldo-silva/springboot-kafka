package io.github.nivaldosilva.validador_boleto.repository;

import io.github.nivaldosilva.validador_boleto.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    boolean existsByCodigoBarras(String codigoBarras);
}