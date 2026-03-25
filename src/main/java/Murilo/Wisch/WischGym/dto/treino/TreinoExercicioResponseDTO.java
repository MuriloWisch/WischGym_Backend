package Murilo.Wisch.WischGym.dto.treino;

import Murilo.Wisch.WischGym.domain.enums.GrupoMuscular;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TreinoExercicioResponseDTO {

    private Long id;

    private Long exercicioId;

    private String exercicioNome;

    private GrupoMuscular grupoMuscular;

    private String gifUrl;

    private Integer series;

    private Integer repeticoes;

    private Integer descanso;

    private String observacao;

    private Integer ordem;

}