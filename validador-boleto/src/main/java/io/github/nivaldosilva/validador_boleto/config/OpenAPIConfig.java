package io.github.nivaldosilva.validador_boleto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Validação e Confirmação de Pagamento de Boletos")
                        .version("v1.0.0")
                        .description("""
                                API RESTful para validação e confirmação de pagamentos de boletos bancários via Kafka.

                                ## Fluxo de pagamento
                                1. O boleto precisa ter sido **validado** previamente pelo serviço de validação
                                2. O endpoint `/pagamentos/confirmar` processa e persiste o pagamento
                                3. Um evento `PAGAMENTO_CONFIRMADO` é publicado no Kafka após a confirmação

                                ## Formatos
                                - Datas: `dd/MM/yyyy`
                                - Valores monetários: decimal com duas casas (ex: `150.00`) em BRL
                                - Código de barras: numérico com exatamente 44 dígitos

                                ## Situações do pagamento
                                | Situação | Descrição |
                                |---|---|
                                | `PAGAMENTO_PENDENTE` | Aguardando confirmação |
                                | `PAGAMENTO_CONFIRMADO` | Pagamento processado com sucesso |
                                | `PAGAMENTO_CANCELADO` | Pagamento cancelado |

                                ## Respostas de erro
                                Todos os erros retornam `tipo`, `titulo`, `status`, `detalhe` e `timestamp`.
                                Erros de validação incluem adicionalmente `campos_invalidos`.
                                """)
                        .contact(new Contact()
                                .name("Nivaldo Silva")
                                .email("nivaldosilva.contato@gmail.com")
                                .url("https://github.com/Nivaldo-Silva"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(new Server()
                        .url("http://localhost:8383")
                        .description("Ambiente local de desenvolvimento")))
                .tags(List.of(new Tag()
                        .name("Pagamentos")
                        .description("Operações relacionadas à validação e confirmação de pagamentos de boletos")));
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public OpenApiCustomizer schemasCustomizer() {
        return openApi -> {
            var schemas = openApi.getComponents().getSchemas();

            schemas.put("ConfirmacaoDePagamento", new ObjectSchema()
                    .description("Dados retornados após a confirmação de um pagamento")
                    .addProperty("id",               new IntegerSchema().format("int64").description("Identificador único do pagamento").example(1))
                    .addProperty("codigo_barras",    new StringSchema().description("Código de barras do boleto (44 dígitos)").example("34191090008200656560881380571000000000010000"))
                    .addProperty("valor",            new NumberSchema().description("Valor pago em reais").example(150.00))
                    .addProperty("descricao",        new StringSchema().description("Descrição informada no pagamento").example("Pagamento referente ao mês de fevereiro"))
                    .addProperty("status_pagamento", new StringSchema().description("Situação resultante do processamento").example("PAGAMENTO_CONFIRMADO"))
                    .addProperty("processado_em",    new StringSchema().description("Data de processamento (dd/MM/yyyy)").example("01/02/2026")));

            schemas.put("ErroDaRequisicao", new ObjectSchema()
                    .description("Erro retornado quando a requisição não pode ser processada")
                    .addProperty("tipo",      new StringSchema().example("https://validador-boleto.github.io/errors/requisicao-invalida"))
                    .addProperty("titulo",    new StringSchema().example("Requisição inválida"))
                    .addProperty("status",    new IntegerSchema().example(409))
                    .addProperty("detalhe",   new StringSchema().example("Pagamento já registrado para o boleto: 34191090008200656560881380571000000000010000"))
                    .addProperty("timestamp", new StringSchema().example("2026-02-27T14:30:00Z")));

            schemas.put("ErroDeValidacao", new ObjectSchema()
                    .description("Erro retornado quando campos da requisição são inválidos")
                    .addProperty("tipo",             new StringSchema().example("https://validador-boleto.github.io/errors/erro-validacao"))
                    .addProperty("titulo",           new StringSchema().example("Erro de validação"))
                    .addProperty("status",           new IntegerSchema().example(422))
                    .addProperty("detalhe",          new StringSchema().example("Boleto não encontrado ou ainda não validado."))
                    .addProperty("timestamp",        new StringSchema().example("2026-02-27T14:30:00Z"))
                    .addProperty("campos_invalidos", new ObjectSchema()
                            .description("Mapa de campo → mensagem de erro")
                            .example(Map.of(
                                    "codigo_barras", "Código de barras deve conter exatamente 44 dígitos numéricos",
                                    "valor", "Valor deve ser maior que zero"))));
        };
    }
}