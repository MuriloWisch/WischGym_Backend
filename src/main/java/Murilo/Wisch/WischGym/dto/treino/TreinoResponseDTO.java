package Murilo.Wisch.WischGym.dto.treino;

import Murilo.Wisch.WischGym.domain.enums.DivisaoTreino;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TreinoResponseDTO {

    private Long id;

    private String nome;

    private String diaTreino;

    private DivisaoTreino divisao;

    private String alunoNome;

    private String professorNome;

    private List<TreinoExercicioResponseDTO> exercicios;

    private LocalDateTime dataCriacao;

    private boolean ativo;
}