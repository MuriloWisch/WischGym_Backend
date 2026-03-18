package Murilo.Wisch.WischGym.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class UserResponse {

    private Long id;
    private String email;
    private Set<String> roles;
}
