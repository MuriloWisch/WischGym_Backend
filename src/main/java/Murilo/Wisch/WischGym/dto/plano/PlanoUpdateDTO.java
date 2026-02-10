package Murilo.Wisch.WischGym.dto.plano;

import java.math.BigDecimal;

public record PlanoUpdateDTO(
        String nome,
        String descricao,
        BigDecimal valor,
        Integer duracaoMeses,
        Boolean ativo
) {

}
