package Murilo.Wisch.WischGym.dto.plano;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PlanoCreateDTO(
         @NotBlank String nome,
                             String descricao,
                             @NotNull @Positive BigDecimal valor,
                             @NotNull @Min(1) Integer duracaoMeses) {
}
