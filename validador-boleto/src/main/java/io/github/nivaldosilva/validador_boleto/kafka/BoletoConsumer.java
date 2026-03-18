package io.github.nivaldosilva.validador_boleto.kafka;

import io.github.nivaldosilva.avro.BoletoEvento;
import io.github.nivaldosilva.validador_boleto.entity.ValidacaoBoleto;
import io.github.nivaldosilva.validador_boleto.enums.StatusValidacao;
import io.github.nivaldosilva.validador_boleto.repository.ValidacaoBoletoRepository;
import io.github.nivaldosilva.validador_boleto.service.ValidarBoletoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoletoConsumer {

    private final ValidacaoBoletoRepository repository;
    private final ValidarBoletoService validarBoletoService;

    @KafkaListener(topics = "${kafka.topics.boleto-emitido}")
    public void consumirBoletoEmitido(@Payload BoletoEvento evento) {
        String codigo = evento.getCodigoBarras() != null ? evento.getCodigoBarras().toString() : "N/A";
        String valor = evento.getValor() != null ? evento.getValor().toString() : "N/A";

        log.info("[CONSUMER] Novo boleto recebido para processamento | codigo={} | valor=R${} | situacao={}",
                codigo, valor, evento.getSituacao());

        if (repository.existsByCodigoBarras(codigo)) {
            log.warn("[CONSUMER] Boleto descartado por já ter sido processado anteriormente | codigo={}", codigo);
            return;
        }

        try {
            StatusValidacao status = validarBoletoService.validarBoleto(codigo, evento);

            repository.save(ValidacaoBoleto.builder()
                    .codigoBarras(codigo)
                    .statusValidacao(status)
                    .build());

            log.info("[CONSUMER] Boleto processado e persistido com sucesso | codigo={} | status={}", codigo, status);

        } catch (Exception e) {
            log.error("[CONSUMER] Erro inesperado ao processar boleto — registro não persistido | codigo={} | erro={}",
                    codigo, e.getMessage());
        }
    }
}