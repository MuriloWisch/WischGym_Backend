package Murilo.Wisch.WischGym.dto.treino;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreinoExercicioDTO {

    @NotNull
    private Long exercicioId;

    private Integer series;

    private Integer repeticoes;

    private Integer descanso;

    private String observacao;

    private Integer ordem;

}