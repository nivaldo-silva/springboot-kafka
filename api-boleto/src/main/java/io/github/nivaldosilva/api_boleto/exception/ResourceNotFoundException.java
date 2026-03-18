package io.github.nivaldosilva.api_boleto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Long id) {
        super("Boleto não encontrado com id: " + id);
    }

    public ResourceNotFoundException(String codigoBarras) {
        super("Boleto não encontrado com código de barras: " + codigoBarras);
    }
}
