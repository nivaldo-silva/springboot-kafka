package io.github.nivaldosilva.api_boleto.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "boletos")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 44, max = 44)
    @Column(name = "codigo_barras",nullable = false, unique = true, length = 44)
    private String codigoBarras;

    @Column(nullable = false)
    private String descricao;

    @NotNull
    @Positive
    @Column(name ="valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(name = "data_emissao", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate dataEmissao;

    @NotNull
    @Column(name = "data_vencimento", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate dataVencimento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusBoleto situacao;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;
}