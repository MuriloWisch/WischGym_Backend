package Murilo.Wisch.WischGym.dto.auth;

import Murilo.Wisch.WischGym.domain.enums.ObjetivoAluno;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegistroRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotNull(message = "Tipo de usuário é obrigatório")
    private Roles tipo;

    private String cref;

    private String cpf;
    private LocalDate dataNascimento;
    private Double peso;
    private Double altura;
    private ObjetivoAluno objetivo;
    private Long planoId;
}
