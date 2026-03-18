package io.github.nivaldosilva.api_boleto.config;

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
import java.util.Set;

@Configuration
public class OpenAPIConfig {

    private static final Set<String> SCHEMAS_PARA_REMOVER = Set.of(
            "Pageable", "PageableObject", "Page", "PageBoletoResponse", "SortObject", "Sort"
    );

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Boletos Bancários")
                        .version("v1.0.0")
                        .description("""
                                API RESTful para gerenciamento completo do ciclo de vida de boletos bancários.

                                ## Funcionalidades
                                - **Emissão** de boletos bancários
                                - **Listagem paginada** com filtros por situação e intervalo de vencimento
                                - **Consulta** por ID ou código de barras
                                - **Atualização** de descrição, valor, vencimento e situação
                                - **Cancelamento** de boletos

                                ## Formatos
                                - Datas: `dd/MM/yyyy`
                                - Timestamps: `dd/MM/yyyy HH:mm:ss`
                                - Valores monetários: decimal com duas casas (ex: `150.00`) em BRL

                                ## Situações possíveis de um boleto
                                | Situação | Descrição |
                                |---|---|
                                | `BOLETO_EMITIDO` | Boleto criado e aguardando validação |
                                | `BOLETO_VALIDADO` | Código de barras validado com sucesso |
                                | `BOLETO_INVALIDO` | Código de barras inválido ou com formato incorreto |
                                | `BOLETO_VENCIDO` | Prazo de pagamento expirado |
                                | `BOLETO_CANCELADO` | Boleto cancelado pelo emissor ou sistema |
                                | `PAGAMENTO_PENDENTE` | Aguardando confirmação de pagamento |
                                | `PAGAMENTO_CONFIRMADO` | Pagamento confirmado com sucesso |
                                | `PAGAMENTO_CANCELADO` | Pagamento cancelado pelo emissor ou sistema |
                                | `ERRO_PROCESSAMENTO` | Falha técnica no processamento |

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
                        .url("http://localhost:8282")
                        .description("Ambiente local de desenvolvimento")))
                .tags(List.of(new Tag()
                        .name("Boletos")
                        .description("Operações relacionadas ao ciclo de vida de boletos bancários")));
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public OpenApiCustomizer schemasCustomizer() {
        return openApi -> {
            var schemas = openApi.getComponents().getSchemas();
            SCHEMAS_PARA_REMOVER.forEach(schemas::remove);

            schemas.put("DadosDoBoleto", new ObjectSchema()
                    .description("Dados completos do boleto retornados pela API")
                    .addProperty("id",              new IntegerSchema().format("int64").description("Identificador único do boleto").example(1))
                    .addProperty("codigo_barras",   new StringSchema().description("Código de barras (44 dígitos)").example("34191090008200656560881380571000000000010000"))
                    .addProperty("descricao",       new StringSchema().description("Descrição do boleto").example("Pagamento referente ao mês de fevereiro"))
                    .addProperty("valor_nominal",   new NumberSchema().description("Valor nominal em reais").example(150.00))
                    .addProperty("data_emissao",    new StringSchema().description("Data de emissão (dd/MM/yyyy)").example("01/02/2026"))
                    .addProperty("data_vencimento", new StringSchema().description("Data de vencimento (dd/MM/yyyy)").example("23/02/2026"))
                    .addProperty("situacao",        new Schema<>().$ref("#/components/schemas/StatusBoleto").description("Situação atual do boleto"))
                    .addProperty("criado_em",       new StringSchema().description("Data e hora de registro (dd/MM/yyyy HH:mm:ss)").example("01/02/2026 10:30:00"))
                    .addProperty("atualizado_em",   new StringSchema().description("Data e hora da última alteração (dd/MM/yyyy HH:mm:ss)").example("01/02/2026 14:45:00")));

            schemas.put("ListaPaginadaDeBoletos", new ObjectSchema()
                    .description("Resultado paginado com lista de boletos e informações de navegação")
                    .addProperty("conteudo",           new ArraySchema().items(new Schema<>().$ref("#/components/schemas/DadosDoBoleto")).description("Boletos da página atual"))
                    .addProperty("pagina_atual",        new IntegerSchema().description("Número da página atual — começa em 0").example(0))
                    .addProperty("tamanho_da_pagina",   new IntegerSchema().description("Quantidade máxima de boletos por página").example(10))
                    .addProperty("total_de_registros",  new IntegerSchema().description("Total de boletos encontrados com os filtros aplicados").example(42))
                    .addProperty("total_de_paginas",    new IntegerSchema().description("Total de páginas disponíveis").example(5))
                    .addProperty("primeira_pagina",     new Schema<Boolean>().type("boolean").description("Indica se é a primeira página").example(true))
                    .addProperty("ultima_pagina",       new Schema<Boolean>().type("boolean").description("Indica se é a última página").example(false)));

            schemas.put("ErroDaRequisicao", new ObjectSchema()
                    .description("Erro retornado quando a requisição não pode ser processada")
                    .addProperty("tipo",      new StringSchema().example("https://api-boleto.github.io/errors/recurso-nao-encontrado"))
                    .addProperty("titulo",    new StringSchema().example("Recurso não encontrado"))
                    .addProperty("status",    new IntegerSchema().example(404))
                    .addProperty("detalhe",   new StringSchema().example("Boleto não encontrado com id: 99"))
                    .addProperty("timestamp", new StringSchema().example("2026-02-27T14:30:00Z")));

            schemas.put("ErroDeValidacao", new ObjectSchema()
                    .description("Erro retornado quando campos da requisição são inválidos")
                    .addProperty("tipo",             new StringSchema().example("https://api-boleto.github.io/errors/erro-validacao"))
                    .addProperty("titulo",           new StringSchema().example("Erro de validação"))
                    .addProperty("status",           new IntegerSchema().example(422))
                    .addProperty("detalhe",          new StringSchema().example("Um ou mais campos estão com valores inválidos."))
                    .addProperty("timestamp",        new StringSchema().example("2026-02-27T14:30:00Z"))
                    .addProperty("campos_invalidos", new ObjectSchema()
                            .description("Mapa de campo → mensagem de erro")
                            .example(Map.of("valor_nominal", "deve ser maior que 0", "data_vencimento", "deve ser uma data futura"))));
        };
    }
}