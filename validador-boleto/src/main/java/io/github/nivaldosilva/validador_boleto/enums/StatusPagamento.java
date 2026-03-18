package io.github.nivaldosilva.validador_boleto.enums;

import lombok.Getter;

@Getter
public enum StatusPagamento {
    PAGAMENTO_PENDENTE,
    PAGAMENTO_CONFIRMADO,
    PAGAMENTO_CANCELADO
}
