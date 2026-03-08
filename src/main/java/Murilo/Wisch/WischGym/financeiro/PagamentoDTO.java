package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.financeiro.enums.FormaPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PagamentoDTO {

    private FormaPagamento formaPagamento;

}
