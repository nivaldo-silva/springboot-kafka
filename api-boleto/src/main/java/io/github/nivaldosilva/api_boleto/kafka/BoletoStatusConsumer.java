package io.github.nivaldosilva.api_boleto.kafka;

import io.github.nivaldosilva.avro.BoletoEvento;
import io.github.nivaldosilva.avro.StatusBoleto;
import io.github.nivaldosilva.api_boleto.entity.Boleto;
import io.github.nivaldosilva.api_boleto.repository.BoletoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoletoStatusConsumer {

    private final BoletoRepository boletoRepository;

    @KafkaListener(
            topics = "${kafka.topics.boleto-pago}",
            groupId = "${kafka.consumer-groups.status}"
    )
    public void consumirResultadoPagamento(@Payload BoletoEvento evento) {
        String codigo = evento.getCodigoBarras() != null
                ? evento.getCodigoBarras().toString() : "N/A";

        log.info("[STATUS] Evento de pagamento recebido | codigo={} | situacao={}",
                codigo, evento.getSituacao());

        if (evento.getSituacao() != StatusBoleto.PAGAMENTO_CONFIRMADO
                && evento.getSituacao() != StatusBoleto.PAGAMENTO_CANCELADO) {
            log.debug("[STATUS] Evento ignorado — não é resultado de pagamento | situacao={}",
                    evento.getSituacao());
            return;
        }

        boletoRepository.findByCodigoBarras(codigo).ifPresentOrElse(
                boleto -> atualizarStatus(boleto, evento.getSituacao()),
                () -> log.warn("[STATUS] Boleto não encontrado no BD local | codigo={}", codigo)
        );
    }

    private void atualizarStatus(Boleto boleto, StatusBoleto situacaoAvro) {
        io.github.nivaldosilva.api_boleto.enums.StatusBoleto novoStatus = mapearStatus(situacaoAvro);
        io.github.nivaldosilva.api_boleto.enums.StatusBoleto statusAnterior = boleto.getSituacao();

        boleto.setSituacao(novoStatus);
        boletoRepository.save(boleto);

        log.info("[STATUS] Boleto atualizado com sucesso | codigo={} | {} → {}",
                boleto.getCodigoBarras(), statusAnterior, novoStatus);
    }

    private io.github.nivaldosilva.api_boleto.enums.StatusBoleto mapearStatus(StatusBoleto situacaoAvro) {
        return switch (situacaoAvro) {
            case PAGAMENTO_CONFIRMADO -> io.github.nivaldosilva.api_boleto.enums.StatusBoleto.PAGAMENTO_CONFIRMADO;
            case PAGAMENTO_CANCELADO  -> io.github.nivaldosilva.api_boleto.enums.StatusBoleto.PAGAMENTO_CANCELADO;
            default                   -> io.github.nivaldosilva.api_boleto.enums.StatusBoleto.ERRO_PROCESSAMENTO;
        };
    }
}
