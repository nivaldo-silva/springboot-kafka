package io.github.nivaldosilva.api_boleto.service;

import io.github.nivaldosilva.api_boleto.dto.BoletoDTO;
import io.github.nivaldosilva.api_boleto.entity.Boleto;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import io.github.nivaldosilva.api_boleto.exception.ConflictException;
import io.github.nivaldosilva.api_boleto.exception.ResourceNotFoundException;
import io.github.nivaldosilva.api_boleto.kafka.BoletoProducer;
import io.github.nivaldosilva.api_boleto.mapper.BoletoMapper;
import io.github.nivaldosilva.api_boleto.repository.BoletoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoletoService {

    private final BoletoRepository repository;
    private final BoletoProducer boletoProducer;

    @Transactional
    public BoletoDTO.BoletoResponse criar(BoletoDTO.BoletoRequest request) {
        log.info("Solicitação de criação de boleto: {}", request.codigoBarras());

        if (repository.existsByCodigoBarras(request.codigoBarras())) {
            log.warn("Negado: Boleto com código de barras {} já existe no sistema.", request.codigoBarras());
            throw new ConflictException(request.codigoBarras());
        }

        Boleto boleto = BoletoMapper.toEntity(request);
        boleto.setSituacao(StatusBoleto.BOLETO_EMITIDO);

        Boleto boletoSalvo = repository.save(boleto);
        BoletoDTO.BoletoResponse response = BoletoMapper.toResponse(boletoSalvo);

        boletoProducer.publicarBoletoEmitido(response);

        return response;
    }

    @Transactional(readOnly = true)
    public Page<BoletoDTO.BoletoResponse> listar(
            StatusBoleto situacao,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) {
        log.info("Listando boletos — situacao: {}, dataInicio: {}, dataFim: {}", situacao, dataInicio, dataFim);
        return repository.listarComFiltros(situacao, dataInicio, dataFim, pageable)
                .map(BoletoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BoletoDTO.BoletoResponse buscarPorId(Long id) {
        log.info("Buscando boleto por ID: {}", id);
        return repository.findById(id)
                .map(BoletoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public BoletoDTO.BoletoResponse buscarPorCodigoBarras(String codigoBarras) {
        log.info("Buscando boleto por código de barras: {}", codigoBarras);
        return repository.findByCodigoBarras(codigoBarras)
                .map(BoletoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(codigoBarras));
    }

    @Transactional
    public BoletoDTO.BoletoResponse atualizar(Long id, BoletoDTO.BoletoAtualizacaoRequest request) {
        log.info("Atualizando boleto ID: {}", id);
        Boleto boleto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        BoletoMapper.atualizarEntidade(boleto, request);
        log.info("Boleto ID: {} atualizado com sucesso", id);
        return BoletoMapper.toResponse(repository.save(boleto));
    }

    @Transactional
    public void cancelar(Long id) {
        log.info("Cancelando boleto ID: {}", id);
        Boleto boleto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        boleto.setSituacao(StatusBoleto.BOLETO_CANCELADO);
        repository.save(boleto);
        log.info("Boleto ID: {} cancelado com sucesso", id);
    }
}