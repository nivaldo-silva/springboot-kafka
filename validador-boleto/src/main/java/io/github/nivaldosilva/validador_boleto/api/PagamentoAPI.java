package io.github.nivaldosilva.validador_boleto.api;

import io.github.nivaldosilva.validador_boleto.controller.request.ConfirmarPagamentoRequest;
import io.github.nivaldosilva.validador_boleto.controller.response.ConfirmarPagamentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Pagamentos", description = "Operações relacionadas à confirmação de pagamentos de boletos")
@RequestMapping("/pagamentos")
public interface PagamentoAPI {

    @Operation(
            summary = "Confirmar pagamento de boleto",
            description = "Valida e confirma o pagamento de um boleto a partir do seu código de barras."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento confirmado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ConfirmarPagamentoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "422", description = "Campos inválidos na requisição",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/confirmar")
    ResponseEntity<ConfirmarPagamentoResponse> confirmarPagamento(
            @RequestBody @Valid ConfirmarPagamentoRequest request
    );
}