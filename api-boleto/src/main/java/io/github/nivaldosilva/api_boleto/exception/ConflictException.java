package io.github.nivaldosilva.api_boleto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException{

    public ConflictException(String codigoBarras) {
        super("Já existe um boleto cadastrado com o código de barras: " + codigoBarras);
    }
}
