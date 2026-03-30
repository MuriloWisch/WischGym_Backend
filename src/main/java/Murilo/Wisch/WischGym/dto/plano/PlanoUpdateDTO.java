package Murilo.Wisch.WischGym.dto.plano;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
public class PlanoUpdateDTO {
    String nome;
    String descricao;
    BigDecimal valor;
    Integer duracaoMeses;
    Boolean ativo;
}
