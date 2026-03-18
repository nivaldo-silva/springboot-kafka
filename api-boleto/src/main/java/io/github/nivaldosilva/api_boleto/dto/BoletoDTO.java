package io.github.nivaldosilva.api_boleto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoletoDTO {

    @Schema(name = "EmissaoDeBoleto", description = "Dados necessários para emitir um novo boleto bancário")
    public record BoletoRequest(

            @NotBlank
            @Size(min = 44, max = 44)
            @JsonProperty("codigo_barras")
            @Schema(description = "Código de barras do boleto (44 dígitos)", example = "34191090008200656560881380571000000000010000")
            String codigoBarras,

            @NotBlank
            @Schema(description = "Descrição do boleto", example = "Pagamento referente ao mês de fevereiro")
            String descricao,

            @NotNull @Positive
            @JsonProperty("valor")
            @Schema(description = "Valor do boleto em reais", example = "150.00")
            BigDecimal valor,

            @NotNull @Future
            @JsonProperty("data_vencimento")
            @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
            @Schema(type = "string", example = "23/02/2026", description = "Data de vencimento no formato dd/MM/yyyy")
            LocalDate dataVencimento

    ) {}

    @Schema(name = "AtualizacaoDeBoleto", description = "Dados para atualizar um boleto existente. O código de barras não pode ser alterado.")
    public record BoletoAtualizacaoRequest(

            @NotBlank
            @Schema(description = "Nova descrição do boleto", example = "Pagamento referente ao mês de março")
            String descricao,

            @NotNull @Positive
            @JsonProperty("valor")
            @Schema(description = "Novo valor do boleto em reais", example = "200.00")
            BigDecimal valor,

            @NotNull @Future
            @JsonProperty("data_vencimento")
            @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
            @Schema(type = "string", example = "30/03/2026", description = "Nova data de vencimento no formato dd/MM/yyyy")
            LocalDate dataVencimento,

            @NotNull
            @Schema(description = "Nova situação do boleto")
            StatusBoleto situacao

    ) {}

    @Schema(name = "DadosDoBoleto", description = "Dados completos do boleto retornados pela API")
    public record BoletoResponse(

            @Schema(description = "Identificador único do boleto")
            Long id,

            @JsonProperty("codigo_barras")
            @Schema(description = "Código de barras do boleto (44 dígitos)")
            String codigoBarras,

            @Schema(description = "Descrição do boleto")
            String descricao,

            @JsonProperty("valor")
            @Schema(description = "Valor do boleto em reais")
            BigDecimal valor,

            @JsonProperty("data_emissao")
            @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
            @Schema(type = "string", example = "01/02/2026", description = "Data de emissão no formato dd/MM/yyyy")
            LocalDate dataEmissao,

            @JsonProperty("data_vencimento")
            @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
            @Schema(type = "string", example = "23/02/2026", description = "Data de vencimento no formato dd/MM/yyyy")
            LocalDate dataVencimento,

            @Schema(description = "Situação atual do boleto")
            StatusBoleto situacao,

            @JsonProperty("criado_em")
            @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
            @Schema(type = "string", example = "01/02/2026 10:30:00", description = "Data e hora de registro no sistema")
            LocalDateTime criadoEm,

            @JsonProperty("atualizado_em")
            @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
            @Schema(type = "string", example = "01/02/2026 14:45:00", description = "Data e hora da última alteração")
            LocalDateTime atualizadoEm

    ) {}
}