package Murilo.Wisch.WischGym.dto.auth;

import Murilo.Wisch.WischGym.domain.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotNull(message = "Tipo de usuario é obrigatória")
    private Roles role;
}