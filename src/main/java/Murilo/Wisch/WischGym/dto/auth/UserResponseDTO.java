package Murilo.Wisch.WischGym.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private Set<String> roles;
    private boolean ativo;
}