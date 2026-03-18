package io.github.nivaldosilva.validador_boleto.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.boleto-notificacao}")
    private String topicoNotificacao;

    @Value("${kafka.topics.boleto-pago}")
    private String topicoPago;

    @Bean
    public NewTopic topicoNotificacao() {
        return TopicBuilder.name(topicoNotificacao)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicoPago() {
        return TopicBuilder.name(topicoPago)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
