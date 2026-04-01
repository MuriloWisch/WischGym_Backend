package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.config.EmailDomainValidator;
import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.entities.specification.AlunoSpecification;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.exception.MatriculaAtivaException;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UserRepository userRepository;
    private final EmailDomainValidator emailDomainValidator;

    public AlunoResponseDTO criar(AlunoCreateDTO dto) {
        if (!emailDomainValidator.isDominioValido(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido. Use um email real.");
        }

        Aluno aluno = Aluno.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .build();

        if (dto.getProfessorId() != null) {
            User professor = userRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

            if (professor.getRoles() == null || !professor.getRoles().contains(Roles.PROFESSOR)) {
                throw new RuntimeException("Usuário informado não é professor");
            }

            aluno.setProfessor(professor);
        }

        return toResponseDTO(alunoRepository.save(aluno));
    }

    public AlunoResponseDTO atualizar(Long id, AlunoCreateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (!aluno.getEmail().equals(dto.getEmail())) {
            if (!emailDomainValidator.isDominioValido(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido. Use um email real.");
            }
        }

        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataNascimento(dto.getDataNascimento());

        if (dto.getProfessorId() != null) {
            User professor = userRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

            if (!professor.getRoles().contains(Roles.PROFESSOR)) {
                throw new RuntimeException("Usuário informado não é professor");
            }

            aluno.setProfessor(professor);
        } else {
            aluno.setProfessor(null);
        }

        return toResponseDTO(alunoRepository.save(aluno));
    }

    public void deletar(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new RuntimeException("Aluno não encontrado");
        }

        if (matriculaRepository.existsByAlunoIdAndStatus(id, StatusMatricula.ATIVA)) {
            throw new MatriculaAtivaException("Não é possível excluir um aluno com matrícula ativa.");
        }

        alunoRepository.deleteById(id);
    }

    public Page<Aluno> listar(String nome, Pageable pageable) {
        if (nome != null && !nome.isEmpty()) {
            return alunoRepository.findByNomeContainingIgnoreCase(nome, pageable);
        }
        return alunoRepository.findAll(pageable);
    }

    public Page<Aluno> listar(String nome, String status, Pageable pageable) {
        Specification<Aluno> spec = Specification.where(null);

        if (nome != null && !nome.isEmpty()) {
            spec = spec.and(AlunoSpecification.nomeContem(nome));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and(AlunoSpecification.statusIgual(status));
        }

        return alunoRepository.findAll(spec, pageable);
    }

    public Page<AlunoResponseDTO> listarPorProfessor(String email, String nome, String status, Pageable pageable) {
        User professor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        return alunoRepository.findByProfessorId(professor.getId(), pageable)
                .map(this::toResponseDTO);
    }

    public List<AlunoResponseDTO> listarTodosPorProfessor(String email) {
        User professor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        return alunoRepository.findByProfessorId(professor.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return toResponseDTO(aluno);
    }

    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        AlunoResponseDTO dto = new AlunoResponseDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setCpf(aluno.getCpf());
        dto.setEmail(aluno.getEmail());
        dto.setTelefone(aluno.getTelefone());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setStatus(aluno.getStatus());
        dto.setDataCadastro(aluno.getDataCadastro());

        if (aluno.getProfessor() != null) {
            dto.setProfessorId(aluno.getProfessor().getId());
            dto.setProfessorNome(aluno.getProfessor().getNome());
        }

        return dto;
    }

    public List<AlunoResponseDTO> listarSemProfessor() {
        return alunoRepository.findAlunosSemProfessor()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
