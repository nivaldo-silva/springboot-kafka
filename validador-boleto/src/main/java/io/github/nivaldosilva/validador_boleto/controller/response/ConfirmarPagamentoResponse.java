package io.github.nivaldosilva.validador_boleto.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.nivaldosilva.validador_boleto.enums.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfirmarPagamentoResponse(
        Long id,
        String codigoBarras,
        BigDecimal valor,
        String descricao,
        StatusPagamento statusPagamento,

        @JsonProperty("processado_em")
        @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
        @Schema(type = "string", example = "01/02/2026", description = "Data de processamento no formato dd/MM/yyyy")
        LocalDateTime processadoEm
) {}
