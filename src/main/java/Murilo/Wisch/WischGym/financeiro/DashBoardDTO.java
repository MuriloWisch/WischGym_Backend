package Murilo.Wisch.WischGym.financeiro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class DashBoardDTO {

    private BigDecimal recebido;
    private BigDecimal pendente;
    private BigDecimal atrasado;
    private BigDecimal receitaMes;

    private long totalAlunos;
    private long totalAtivos;
    private long totalInativos;
    private long totalInadimplentes;
    private long novosNoMes;
}
