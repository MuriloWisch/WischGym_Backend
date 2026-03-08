package Murilo.Wisch.WischGym.dto.aluno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AlunoCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, message = "Nome deve ter pelo menos 3 caracteres")
    private String nome;

    @NotBlank
    private String cpf;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    private String telefone;

    @NotNull
    private LocalDate dataNascimento;
}
