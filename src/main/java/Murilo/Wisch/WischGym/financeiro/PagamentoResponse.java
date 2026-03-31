package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResponse(Long id,
                                String nomePlano,
                                BigDecimal valor,
                                StatusPagamento status,
                                LocalDate dataPagamento,
                                LocalDate dataVencimento,
                                LocalDate periodoInicio,
                                LocalDate periodoFim,
                                String nomeAluno
) {}