package io.github.nivaldosilva.validador_boleto.entity;

import io.github.nivaldosilva.validador_boleto.enums.StatusValidacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "validacoes_boleto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacaoBoleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barras", nullable = false, length = 44, unique = true)
    private String codigoBarras;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_validacao", nullable = false)
    private StatusValidacao statusValidacao;

    @CreationTimestamp
    @Column(name = "validado_em", updatable = false, nullable = false)
    private LocalDateTime validadoEm;
}