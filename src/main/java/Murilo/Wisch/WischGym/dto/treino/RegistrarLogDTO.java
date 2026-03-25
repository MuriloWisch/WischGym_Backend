package Murilo.Wisch.WischGym.dto.treino;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RegistrarLogDTO {

    private Long treinoId;
    private List<Long> exerciciosConcluidos;

}