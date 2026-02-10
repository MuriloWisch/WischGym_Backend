package Murilo.Wisch.WischGym.dto.plano;

import java.math.BigDecimal;

public record PlanoResponseDTO(Long id,
                               String nome,
                               String descricao,
                               BigDecimal valor,
                               Integer duracaoMeses,
                               boolean ativo) {
}
