package io.github.nivaldosilva.validador_boleto.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record ConfirmarPagamentoRequest(

        @NotBlank(message = "Código de barras é obrigatório")
        @Pattern(regexp = "\\d{44}", message = "Código de barras deve conter exatamente 44 dígitos numéricos")
        String codigoBarras,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal valor,

        String descricao
) {}