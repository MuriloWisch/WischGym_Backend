package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

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

    public Page<AlunoResponseDTO> listar(Pageable pageable) {
        return alunoRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public AlunoResponseDTO buscarPorId(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return toResponseDTO(aluno);
    }


}
