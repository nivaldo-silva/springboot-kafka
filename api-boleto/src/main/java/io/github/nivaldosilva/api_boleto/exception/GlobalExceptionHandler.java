package io.github.nivaldosilva.api_boleto.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_URI_ERROS = "https://api-boleto.github.io/errors";

    @ApiResponse(
            responseCode = "404",
            description = "Boleto não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso não encontrado");
        problem.setType(URI.create(BASE_URI_ERROS + "/recurso-nao-encontrado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ApiResponse(
            responseCode = "409",
            description = "Código de barras já cadastrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflito de dados");
        problem.setType(URI.create(BASE_URI_ERROS + "/codigo-barras-duplicado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ApiResponse(
            responseCode = "422",
            description = "Campos do boleto inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> camposInvalidos = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "valor inválido",
                        (existente, novo) -> existente
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Um ou mais campos estão com valores inválidos. Verifique os detalhes em 'campos_invalidos'."
        );
        problem.setTitle("Erro de validação");
        problem.setType(URI.create(BASE_URI_ERROS + "/erro-validacao"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("campos_invalidos", camposInvalidos);
        return problem;
    }

    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde."
        );
        problem.setTitle("Erro interno do servidor");
        problem.setType(URI.create(BASE_URI_ERROS + "/erro-interno"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}