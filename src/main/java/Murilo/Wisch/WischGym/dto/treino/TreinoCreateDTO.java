package Murilo.Wisch.WischGym.dto.treino;

import Murilo.Wisch.WischGym.domain.enums.DivisaoTreino;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TreinoCreateDTO {

    @NotBlank
    private String nome;

    private String diaTreino;

    @NotNull
    private DivisaoTreino divisao;

    @NotNull
    private Long alunoId;

    @NotNull
    private List<TreinoExercicioDTO> exercicios;

}