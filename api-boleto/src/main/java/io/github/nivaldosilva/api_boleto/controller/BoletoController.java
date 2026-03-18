package io.github.nivaldosilva.api_boleto.controller;

import io.github.nivaldosilva.api_boleto.api.BoletoAPI;
import io.github.nivaldosilva.api_boleto.dto.BoletoDTO;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import io.github.nivaldosilva.api_boleto.service.BoletoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class BoletoController implements BoletoAPI {

    private final BoletoService service;

    @Override
    public ResponseEntity<BoletoDTO.BoletoResponse> emitir(BoletoDTO.BoletoRequest request) {
        BoletoDTO.BoletoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<Page<BoletoDTO.BoletoResponse>> listar(
            StatusBoleto situacao,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable) {
        return ResponseEntity.ok(service.listar(situacao, dataInicio, dataFim, pageable));
    }

    @Override
    public ResponseEntity<BoletoDTO.BoletoResponse> buscarPorId(Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Override
    public ResponseEntity<BoletoDTO.BoletoResponse> buscarPorCodigoBarras(String codigoBarras) {
        return ResponseEntity.ok(service.buscarPorCodigoBarras(codigoBarras));
    }

    @Override
    public ResponseEntity<BoletoDTO.BoletoResponse> atualizar(Long id, BoletoDTO.BoletoAtualizacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Override
    public ResponseEntity<Void> cancelar(Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}