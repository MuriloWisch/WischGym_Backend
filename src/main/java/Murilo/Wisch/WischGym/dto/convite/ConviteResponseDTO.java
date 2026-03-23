package Murilo.Wisch.WischGym.dto.convite;

import Murilo.Wisch.WischGym.domain.enums.StatusConvite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ConviteResponseDTO {

    private Long id;
    private String professorNome;
    private String professorEmail;
    private String alunoNome;
    private String alunoEmail;
    private StatusConvite status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataResposta;
}
