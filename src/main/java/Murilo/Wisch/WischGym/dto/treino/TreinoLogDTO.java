package Murilo.Wisch.WischGym.dto.treino;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TreinoLogDTO {

    private Long id;
    private LocalDate data;
    private Integer porcentagemConcluida;
    private boolean concluido;
    private TreinoResumoDTO treino;

}
