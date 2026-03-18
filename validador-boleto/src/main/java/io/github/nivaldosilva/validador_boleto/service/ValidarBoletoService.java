package io.github.nivaldosilva.validador_boleto.service;

import io.github.nivaldosilva.avro.BoletoEvento;
import io.github.nivaldosilva.avro.StatusBoleto;
import io.github.nivaldosilva.validador_boleto.enums.StatusValidacao;
import io.github.nivaldosilva.validador_boleto.kafka.ValidacaoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidarBoletoService {

    private final ValidacaoProducer notificacaoProducer;

    public StatusValidacao validarBoleto(String codigo, BoletoEvento eventoOriginal) {
        StatusValidacao status;

        if (codigo == null || codigo.isBlank()) {
            log.warn("[VALIDACAO] Boleto reprovado por ausência de código de barras");
            status = StatusValidacao.REPROVADO;
        } else if (!codigo.matches("\\d{44}")) {
            log.warn("[VALIDACAO] Boleto reprovado por formato inválido | codigo={} | tamanho_esperado=44 | tamanho_recebido={}",
                    codigo, codigo.length());
            status = StatusValidacao.REPROVADO;
        } else {
            log.info("[VALIDACAO] Boleto aprovado com sucesso | codigo={}", codigo);
            status = StatusValidacao.APROVADO;
        }

        enviarNotificacao(eventoOriginal, status);
        return status;
    }

    private void enviarNotificacao(BoletoEvento eventoOriginal, StatusValidacao status) {
        try {
            BoletoEvento eventoNotificacao = BoletoEvento.newBuilder()
                    .setCodigoBarras(eventoOriginal.getCodigoBarras())
                    .setDescricao(eventoOriginal.getDescricao())
                    .setValor(eventoOriginal.getValor())
                    .setSituacao(mapearStatus(status))
                    .build();

            notificacaoProducer.enviarMensagem(eventoNotificacao);
            log.info("[NOTIFICACAO] Evento de validação publicado com sucesso | codigo={} | status={}",
                    eventoOriginal.getCodigoBarras(), status);

        } catch (Exception e) {
            log.error("[NOTIFICACAO] Falha ao publicar evento de validação | codigo={} | erro={}",
                    eventoOriginal.getCodigoBarras(), e.getMessage());
        }
    }

    private StatusBoleto mapearStatus(StatusValidacao status) {
        return switch (status) {
            case APROVADO -> StatusBoleto.BOLETO_VALIDADO;
            case REPROVADO -> StatusBoleto.BOLETO_INVALIDO;
        };
    }

    }
