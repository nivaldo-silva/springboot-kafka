package io.github.nivaldosilva.validador_boleto.kafka;

import io.github.nivaldosilva.avro.BoletoEvento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidacaoProducer {

    private final KafkaTemplate<String, BoletoEvento> kafkaTemplate;

    @Value("${kafka.topics.boleto-notificacao}")
    private String topico;

    public void enviarMensagem(BoletoEvento evento) {
        String codigo = evento.getCodigoBarras() != null ? evento.getCodigoBarras().toString() : "N/A";

        log.info("[PRODUCER] Iniciando publicação de notificação de boleto | topico={} | codigo={} | situacao={}",
                topico, codigo, evento.getSituacao());

        kafkaTemplate.send(topico, codigo, evento)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[PRODUCER] Não foi possível entregar o evento ao broker Kafka — verifique conectividade e configurações | topico={} | codigo={} | causa={}",
                                topico, codigo, ex.getMessage());
                    } else {
                        log.info("[PRODUCER] Notificação de boleto entregue ao broker com sucesso | topico={} | codigo={} | situacao={} | particao={} | offset={}",
                                topico,
                                codigo,
                                evento.getSituacao(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}