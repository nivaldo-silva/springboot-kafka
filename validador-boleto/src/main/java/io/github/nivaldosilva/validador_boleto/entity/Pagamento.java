package io.github.nivaldosilva.validador_boleto.entity;

import io.github.nivaldosilva.validador_boleto.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barras", nullable = false, length = 44, unique = true)
    private String codigoBarras;

    @Column(name = "valor", precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamento statusPagamento;

    @CreationTimestamp
    @Column(name = "processado_em", updatable = false, nullable = false)
    private LocalDateTime processadoEm;
}
