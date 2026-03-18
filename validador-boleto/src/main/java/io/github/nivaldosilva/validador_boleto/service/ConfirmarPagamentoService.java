package io.github.nivaldosilva.validador_boleto.service;

import io.github.nivaldosilva.avro.BoletoEvento;
import io.github.nivaldosilva.avro.StatusBoleto;
import io.github.nivaldosilva.validador_boleto.controller.request.ConfirmarPagamentoRequest;
import io.github.nivaldosilva.validador_boleto.controller.response.ConfirmarPagamentoResponse;
import io.github.nivaldosilva.validador_boleto.entity.Pagamento;
import io.github.nivaldosilva.validador_boleto.enums.StatusPagamento;
import io.github.nivaldosilva.validador_boleto.kafka.PagamentoProducer;
import io.github.nivaldosilva.validador_boleto.repository.PagamentoRepository;
import io.github.nivaldosilva.validador_boleto.repository.ValidacaoBoletoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmarPagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ValidacaoBoletoRepository validacaoBoletoRepository;
    private final PagamentoProducer pagamentoProducer;

    public ConfirmarPagamentoResponse confirmar(ConfirmarPagamentoRequest request) {
        String codigo = request.codigoBarras();

        if (!validacaoBoletoRepository.existsByCodigoBarras(codigo)) {
            log.warn("[PAGAMENTO] Tentativa de pagar boleto não validado | codigo={}", codigo);
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Boleto não encontrado ou ainda não validado: " + codigo
            );
        }

        if (pagamentoRepository.existsByCodigoBarras(codigo)) {
            log.warn("[PAGAMENTO] Tentativa de pagamento duplicado | codigo={}", codigo);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pagamento já registrado para o boleto: " + codigo
            );
        }

        Pagamento pagamento = pagamentoRepository.save(
                Pagamento.builder()
                        .codigoBarras(codigo)
                        .valor(request.valor())
                        .descricao(request.descricao())
                        .statusPagamento(StatusPagamento.PAGAMENTO_CONFIRMADO)
                        .build()
        );

        log.info("[PAGAMENTO] Pagamento confirmado e salvo | codigo={} | valor={}", codigo, request.valor());


        publicarEvento(pagamento);
        return toResponse(pagamento);
    }

    private void publicarEvento(Pagamento pagamento) {
        try {
            BoletoEvento evento = BoletoEvento.newBuilder()
                    .setCodigoBarras(pagamento.getCodigoBarras())
                    .setDescricao(pagamento.getDescricao() != null ? pagamento.getDescricao() : "")
                    .setValor(pagamento.getValor().toPlainString())
                    .setSituacao(StatusBoleto.PAGAMENTO_CONFIRMADO)
                    .build();

            pagamentoProducer.enviarMensagem(evento);
            log.info("[PAGAMENTO] Evento PAGAMENTO_CONFIRMADO publicado | codigo={}", pagamento.getCodigoBarras());

        } catch (Exception e) {

            log.error("[PAGAMENTO] Falha ao publicar evento após confirmação | codigo={} | erro={}",
                    pagamento.getCodigoBarras(), e.getMessage());
        }
    }

    private ConfirmarPagamentoResponse toResponse(Pagamento pagamento) {
        return new ConfirmarPagamentoResponse(
                pagamento.getId(),
                pagamento.getCodigoBarras(),
                pagamento.getValor(),
                pagamento.getDescricao(),
                pagamento.getStatusPagamento(),
                pagamento.getProcessadoEm()
        );
    }
}