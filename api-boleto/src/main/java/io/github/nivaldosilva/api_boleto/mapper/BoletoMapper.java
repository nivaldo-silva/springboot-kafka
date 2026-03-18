package io.github.nivaldosilva.api_boleto.mapper;

import io.github.nivaldosilva.api_boleto.dto.BoletoDTO;
import io.github.nivaldosilva.api_boleto.entity.Boleto;
import io.github.nivaldosilva.api_boleto.enums.StatusBoleto;
import lombok.experimental.UtilityClass;
import java.time.LocalDate;
import java.time.ZoneId;

@UtilityClass
public class BoletoMapper {

    public static Boleto toEntity(BoletoDTO.BoletoRequest request) {
        return Boleto.builder()
                .codigoBarras(request.codigoBarras())
                .descricao(request.descricao())
                .valor(request.valor())
                .dataEmissao(LocalDate.now(ZoneId.of("America/Sao_Paulo")))
                .dataVencimento(request.dataVencimento())
                .situacao(StatusBoleto.PAGAMENTO_PENDENTE)
                .build();
    }

    public static BoletoDTO.BoletoResponse toResponse(Boleto boleto) {
        return new BoletoDTO.BoletoResponse(
                boleto.getId(),
                boleto.getCodigoBarras(),
                boleto.getDescricao(),
                boleto.getValor(),
                boleto.getDataEmissao(),
                boleto.getDataVencimento(),
                boleto.getSituacao(),
                boleto.getCriadoEm(),
                boleto.getAtualizadoEm()
        );
    }

    public static void atualizarEntidade(Boleto boleto, BoletoDTO.BoletoAtualizacaoRequest request) {
        boleto.setDescricao(request.descricao());
        boleto.setValor(request.valor());
        boleto.setDataVencimento(request.dataVencimento());
        boleto.setSituacao(request.situacao());
    }
}