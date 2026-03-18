package io.github.nivaldosilva.api_boleto.kafka;

import io.github.nivaldosilva.api_boleto.dto.BoletoDTO;
import io.github.nivaldosilva.avro.BoletoEvento;
import io.github.nivaldosilva.avro.StatusBoleto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoletoProducer {

    @Value("${kafka.topics.boleto-emitido}")
    private String topicName;

    private final KafkaTemplate<String, BoletoEvento> kafkaTemplate;

    public void publicarBoletoEmitido(BoletoDTO.BoletoResponse dto) {
        String chave = dto.codigoBarras();

        String valorStr = dto.valor() != null
                ? dto.valor().setScale(2, RoundingMode.HALF_UP).toPlainString()
                : null;

        BoletoEvento evento = BoletoEvento.newBuilder()
                .setCodigoBarras(chave)
                .setDescricao(dto.descricao())
                .setValor(valorStr)
                .setSituacao(StatusBoleto.valueOf(dto.situacao().name()))
                .build();

        log.info("[PRODUCER] Enviando boleto | codigo={} | valor={} | situacao={}", chave, valorStr, dto.situacao());

        kafkaTemplate.send(topicName, chave, evento)
                .whenComplete((resultado, erro) -> {
                    if (erro == null) {
                        log.info("[PRODUCER] Enviado com sucesso | codigo={} | particao={} | offset={}",
                                chave,
                                resultado.getRecordMetadata().partition(),
                                resultado.getRecordMetadata().offset());
                    } else {
                        log.error("[PRODUCER] Falha ao enviar | codigo={} | erro={}", chave, erro.getMessage());
                    }
                });
    }
}