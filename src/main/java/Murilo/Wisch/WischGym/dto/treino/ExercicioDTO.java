package Murilo.Wisch.WischGym.dto.treino;

import Murilo.Wisch.WischGym.domain.enums.GrupoMuscular;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExercicioDTO {

    @NotBlank
    private String nome;

    @NotNull
    private GrupoMuscular grupoMuscular;

    private String gifUrl;

    private String descricao;

}