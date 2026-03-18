package io.github.nivaldosilva.validador_boleto.controller;


import io.github.nivaldosilva.validador_boleto.controller.request.ConfirmarPagamentoRequest;
import io.github.nivaldosilva.validador_boleto.controller.response.ConfirmarPagamentoResponse;
import io.github.nivaldosilva.validador_boleto.service.ConfirmarPagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final ConfirmarPagamentoService confirmarPagamentoService;

    @PostMapping("/confirmar")
    public ResponseEntity<ConfirmarPagamentoResponse> confirmarPagamento(
            @RequestBody @Valid ConfirmarPagamentoRequest request) {

        log.info("[CONTROLLER] Requisição de confirmação de pagamento recebida | codigo={}", request.codigoBarras());

        ConfirmarPagamentoResponse response = confirmarPagamentoService.confirmar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}