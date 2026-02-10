package Murilo.Wisch.WischGym.dto.plano;

import java.math.BigDecimal;

public record PlanoCreateDTO(String nome,
                             String descricao,
                             BigDecimal valor,
                             Integer duracaoMeses) {
}
