package Murilo.Wisch.WischGym.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
public class RelatorioMensalDTO {

    private int year;
    private int month;

    private long totalAlunos;
    private long totalAtivos;
    private long totalInadimplentes;

    private long novosAlunosNoMes;

}
