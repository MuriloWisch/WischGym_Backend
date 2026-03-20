package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.entities.specification.AlunoSpecification;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.exception.MatriculaAtivaException;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoResponseDTO criar(AlunoCreateDTO dto){
        Aluno aluno = Aluno.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento()).build();

        Aluno salvo = alunoRepository.save(aluno);
        return toResponseDTO(salvo);
    }

    public Page<Aluno> listar(String nome, Pageable pageable){

        if(nome != null && !nome.isEmpty()){
            return alunoRepository.findByNomeContainingIgnoreCase(nome, pageable);
        }

        return alunoRepository.findAll(pageable);
    }

    public Page<Aluno> listar(String nome, String status, Pageable pageable){

        Specification<Aluno> spec = Specification.where(null);

        if(nome != null && !nome.isEmpty()){
            spec = spec.and(AlunoSpecification.nomeContem(nome));
        }

        if(status != null && !status.isEmpty()){
            spec = spec.and(AlunoSpecification.statusIgual(status));
        }

        return alunoRepository.findAll(spec, pageable);
    }

    public AlunoResponseDTO buscarPorId(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return toResponseDTO(aluno);
    }

    public AlunoResponseDTO atualizar(Long id, AlunoCreateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataNascimento(dto.getDataNascimento());

        return toResponseDTO(alunoRepository.save(aluno));
    }

    public void deletar(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        boolean temMatriculaAtiva = matriculaRepository.existsByAlunoAndStatus(aluno, StatusMatricula.ATIVA);

        if (temMatriculaAtiva) {
            throw new MatriculaAtivaException("Não é possível excluir um aluno com matrícula ativa.");
        }

        alunoRepository.delete(aluno);
    }

    private AlunoResponseDTO toResponseDTO(Aluno aluno){
        AlunoResponseDTO dto = new AlunoResponseDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setCpf(aluno.getCpf());
        dto.setEmail(aluno.getEmail());
        dto.setTelefone(aluno.getTelefone());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setStatus(aluno.getStatus());
        dto.setDataCadastro(aluno.getDataCadastro());
        return dto;
    }
}
