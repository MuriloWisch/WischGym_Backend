package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.domain.RefreshToken;
import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.dto.auth.*;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.PlanoRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import Murilo.Wisch.WischGym.security.jwt.JwtService;
import Murilo.Wisch.WischGym.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, AlunoRepository alunoRepository, PlanoRepository planoRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalStateException("Usuário sem role definida");
        }

        String accessToken = jwtService.generateToken(user.getEmail());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        Set<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                roles
        );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                userResponse
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@RequestBody @Valid RegistroRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (request.getTipo() == Roles.PROFESSOR &&
                (request.getCref() == null || request.getCref().isBlank())) {
            throw new RuntimeException("CREF é obrigatório para professores");
        }

        if (request.getTipo() == Roles.ALUNO &&
                (request.getCpf() == null || request.getCpf().isBlank())) {
            throw new RuntimeException("CPF é obrigatório para alunos");
        }

        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getSenha()))
                .roles(Set.of(request.getTipo()))
                .active(true)
                .build();

        userRepository.save(user);

        if (request.getTipo() == Roles.ALUNO) {
            Aluno aluno = Aluno.builder()
                    .nome(request.getNome())
                    .cpf(request.getCpf())
                    .email(request.getEmail())
                    .dataNascimento(request.getDataNascimento())
                    .peso(request.getPeso())
                    .altura(request.getAltura())
                    .objetivo(request.getObjetivo())
                    .user(user)
                    .build();

            if (request.getPlanoId() != null) {
                planoRepository.findById(request.getPlanoId())
                        .ifPresent(aluno::setPlano);
            }

            alunoRepository.save(aluno);
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), roles);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken(), userResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request){

        refreshTokenService.deleteByToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
