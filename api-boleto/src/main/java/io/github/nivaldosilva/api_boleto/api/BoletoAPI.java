package io.github.nivaldosilva.api_boleto.api;

import io.github.nivaldosilva.api_boleto.dto.BoletoDTO;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Tag(name = "Boletos", description = "Operações relacionadas ao ciclo de vida de boletos bancários")
@RequestMapping("/api/v1/boletos")
public interface BoletoAPI {

    @Operation(
            summary = "Emitir novo boleto",
            description = "Registra um novo boleto bancário no sistema com situação inicial `PAGAMENTO_PENDENTE`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Boleto emitido com sucesso",
                    headers = @Header(name = "Location", description = "URI do boleto criado"),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/DadosDoBoleto"))),
            @ApiResponse(responseCode = "409", description = "Já existe um boleto com o mesmo código de barras",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDaRequisicao"))),
            @ApiResponse(responseCode = "422", description = "Campos do boleto inválidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDeValidacao")))
    })
    @PostMapping
    ResponseEntity<BoletoDTO.BoletoResponse> emitir(
            @RequestBody @Valid BoletoDTO.BoletoRequest request
    );

    @Operation(
            summary = "Listar boletos",
            description = "Retorna uma lista paginada de boletos com filtros opcionais por situação e intervalo de data de vencimento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ListaPaginadaDeBoletos"))),
            @ApiResponse(responseCode = "422", description = "Parâmetros de filtro inválidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDeValidacao")))
    })
    @GetMapping
    ResponseEntity<Page<BoletoDTO.BoletoResponse>> listar(
            @Parameter(description = "Filtrar por situação do boleto", example = "PAGAMENTO_PENDENTE")
            @RequestParam(required = false) StatusBoleto situacao,

            @Parameter(description = "Data de vencimento inicial no formato dd/MM/yyyy", example = "01/02/2026")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,

            @Parameter(description = "Data de vencimento final no formato dd/MM/yyyy", example = "28/02/2026")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,

            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "dataVencimento", direction = Sort.Direction.ASC)
            Pageable pageable
    );

    @Operation(
            summary = "Buscar boleto por ID",
            description = "Retorna os dados completos de um boleto a partir do seu identificador único."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleto encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/DadosDoBoleto"))),
            @ApiResponse(responseCode = "404", description = "Boleto não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDaRequisicao")))
    })
    @GetMapping("/{id}")
    ResponseEntity<BoletoDTO.BoletoResponse> buscarPorId(
            @Parameter(description = "Identificador único do boleto", example = "1")
            @PathVariable Long id
    );

    @Operation(
            summary = "Buscar boleto por código de barras",
            description = "Retorna os dados completos de um boleto a partir do seu código de barras de 44 dígitos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleto encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/DadosDoBoleto"))),
            @ApiResponse(responseCode = "404", description = "Boleto não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDaRequisicao")))
    })
    @GetMapping("/codigo-barras/{codigoBarras}")
    ResponseEntity<BoletoDTO.BoletoResponse> buscarPorCodigoBarras(
            @Parameter(description = "Código de barras do boleto (44 dígitos)", example = "34191090008200656560881380571000000000010000")
            @PathVariable String codigoBarras
    );

    @Operation(
            summary = "Atualizar boleto",
            description = "Atualiza os dados de um boleto existente. O código de barras não pode ser alterado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleto atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/DadosDoBoleto"))),
            @ApiResponse(responseCode = "404", description = "Boleto não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDaRequisicao"))),
            @ApiResponse(responseCode = "422", description = "Dados inválidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDeValidacao")))
    })
    @PutMapping("/{id}")
    ResponseEntity<BoletoDTO.BoletoResponse> atualizar(
            @Parameter(description = "Identificador único do boleto", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid BoletoDTO.BoletoAtualizacaoRequest request
    );

    @Operation(
            summary = "Cancelar boleto",
            description = "Cancela um boleto alterando sua situação para `PAGAMENTO_CANCELADO`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Boleto cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Boleto não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErroDaRequisicao")))
    })
    @PatchMapping("/{id}/cancelar")
    ResponseEntity<Void> cancelar(
            @Parameter(description = "Identificador único do boleto", example = "1")
            @PathVariable Long id
    );
}