package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.dto.auth.UserCreateDTO;
import Murilo.Wisch.WischGym.dto.auth.UserResponseDTO;
import Murilo.Wisch.WischGym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO criar(UserCreateDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getSenha()))
                .roles(Set.of(dto.getRole()))
                .active(true)
                .build();

        return toResponseDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> listarProfessores() {
        return userRepository.findByRole(Roles.PROFESSOR)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> listarTodos() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));
        dto.setAtivo(user.isActive());
        return dto;
    }
}