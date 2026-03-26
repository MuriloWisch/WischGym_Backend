package Murilo.Wisch.WischGym.dto.matricula;

import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MatriculaDetalheResponse(
        Long matriculaId,
        String nomePlano,
        BigDecimal valorPlano,
        Integer duracaoMeses,
        LocalDate dataInicio,
        LocalDate dataVencimento,
        StatusMatricula statusMatricula,
        Long pagamentoPendenteId,
        StatusPagamento statusPagamento,
        LocalDate dataPagamento
) {}