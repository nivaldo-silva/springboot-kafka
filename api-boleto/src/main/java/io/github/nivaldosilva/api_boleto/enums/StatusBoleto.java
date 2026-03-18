package io.github.nivaldosilva.api_boleto.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true, description = "Status do ciclo de vida do boleto")
public enum StatusBoleto {

    @Schema(description = "Boleto emitido e aguardando validação")
    BOLETO_EMITIDO,

    @Schema(description = "Código de barras validado com sucesso")
    BOLETO_VALIDADO,

    @Schema(description = "Código de barras inválido ou com formato incorreto")
    BOLETO_INVALIDO,

    @Schema(description = "Boleto cancelado pelo emissor ou sistema")
    BOLETO_CANCELADO,

    @Schema(description = "Prazo de pagamento expirado")
    BOLETO_VENCIDO,

    @Schema(description = "Boleto validado e aguardando pagamento")
    PAGAMENTO_PENDENTE,

    @Schema(description = "Pagamento confirmado com sucesso")
    PAGAMENTO_CONFIRMADO,

    @Schema(description = "Pagamento cancelado pelo emissor ou sistema")
    PAGAMENTO_CANCELADO,

    @Schema(description = "Falha técnica no processamento do boleto ou pagamento")
    ERRO_PROCESSAMENTO;
}