package Murilo.Wisch.WischGym.dto.treino;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProgressoDTO {
    private long treinosSemana;
    private long treinosMes;
    private long treinosTotais;
    private double mediaConsistencia;
    private int sequenciaDias;
}