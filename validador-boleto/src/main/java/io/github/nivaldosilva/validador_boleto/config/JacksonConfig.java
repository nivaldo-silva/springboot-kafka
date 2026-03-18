package io.github.nivaldosilva.validador_boleto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter FORMATO_DATA            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA       = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();

        module.addSerializer(LocalDate.class,     new LocalDateSerializer(FORMATO_DATA));
        module.addDeserializer(LocalDate.class,   new LocalDateDeserializer(FORMATO_DATA));

        module.addSerializer(LocalDateTime.class,   new LocalDateTimeSerializer(FORMATO_DATA_HORA));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATO_DATA_HORA));

        return JsonMapper.builder()
                .addModule(module)
                .build();
    }
}