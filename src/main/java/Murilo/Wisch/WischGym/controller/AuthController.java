package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.config.EmailDomainValidator;
import Murilo.Wisch.WischGym.domain.RefreshToken;
import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;
import Murilo.Wisch.WischGym.dto.auth.*;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.PlanoRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import Murilo.Wisch.WischGym.security.jwt.JwtService;
import Murilo.Wisch.WischGym.service.NotificacaoService;
import Murilo.Wisch.WischGym.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final NotificacaoService notificacaoService;
    private final EmailDomainValidator emailDomainValidator;
    private final Validator validator;



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


        List<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        String accessToken = jwtService.generateToken(user.getEmail(), roles, user.getId());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        Set<String> rolesSet = new HashSet<>(roles);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                rolesSet
        );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                userResponse
        );
    }

    @PostMapping("/registro")
    @Transactional
    public ResponseEntity<AuthResponse> registro(@RequestBody RegistroRequest request) {

        if (request.getTipo() == Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Não é permitido criar conta admin por este endpoint.");
        }

        if (!emailDomainValidator.isDominioValido(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email inválido. Use um email real.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email já cadastrado.");
        }

        if (request.getTipo() == Roles.PROFESSOR &&
                (request.getCref() == null || request.getCref().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CREF é obrigatório para professores.");
        }

        if (request.getTipo() == Roles.ALUNO &&
                (request.getCpf() == null || request.getCpf().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CPF é obrigatório para alunos.");
        }

        if (request.getTipo() == Roles.ALUNO) {
            Aluno alunoParaValidar = Aluno.builder()
                    .nome(request.getNome())
                    .cpf(request.getCpf())
                    .email(request.getEmail())
                    .dataNascimento(request.getDataNascimento())
                    .peso(request.getPeso())
                    .altura(request.getAltura())
                    .objetivo(request.getObjetivo())
                    .build();

            Set<ConstraintViolation<Aluno>> violations = validator.validate(alunoParaValidar);
            if (!violations.isEmpty()) {
                List<String> erros = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .sorted()
                        .toList();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.join(", ", erros));
            }
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

            notificarAdminsEProfessores(request.getNome());
        }

        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        String accessToken = jwtService.generateToken(user.getEmail(), rolesList, user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), new HashSet<>(rolesList));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(accessToken, refreshToken.getToken(), userResponse));
    }


    private void notificarAdminsEProfessores(String nomeAluno) {
        String titulo = "Novo aluno cadastrado";
        String mensagem = "O aluno " + nomeAluno + " acabou de se cadastrar na plataforma.";

        Stream.concat(
                userRepository.findByRolesContaining(Roles.ADMIN).stream(),
                userRepository.findByRolesContaining(Roles.PROFESSOR).stream()
        ).forEach(destinatario ->
                notificacaoService.criar(destinatario, TipoNotificacao.NOVO_ALUNO, titulo, mensagem)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        RefreshToken novoRefreshToken = refreshTokenService.validarERotacionar(request.refreshToken());

        User user = novoRefreshToken.getUser();

        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        String accessToken = jwtService.generateToken(user.getEmail(), rolesList, user.getId());
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                new HashSet<>(rolesList)
        );

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                novoRefreshToken.getToken(),
                userResponse
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request){

        refreshTokenService.deleteByToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
