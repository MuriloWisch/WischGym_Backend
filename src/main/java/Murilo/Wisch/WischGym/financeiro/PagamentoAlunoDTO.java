package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PagamentoAlunoDTO {

    private Long id;
    private BigDecimal valor;
    private StatusPagamento status;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;

}
