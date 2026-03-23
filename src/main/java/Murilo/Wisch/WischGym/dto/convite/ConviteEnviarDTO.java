package Murilo.Wisch.WischGym.dto.convite;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConviteEnviarDTO {

    @Email
    @NotBlank(message = "Email do aluno é obrigatório")
    private String emailAluno;
}
