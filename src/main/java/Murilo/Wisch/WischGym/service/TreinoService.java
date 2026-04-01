package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.*;

import Murilo.Wisch.WischGym.dto.treino.*;
import Murilo.Wisch.WischGym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final ExercicioRepository exercicioRepository;
    private final AlunoRepository alunoRepository;
    private final UserRepository userRepository;

    public TreinoResponseDTO criar(String emailProfessor, TreinoCreateDTO dto) {
        User professor = userRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Treino treino = Treino.builder()
                .nome(dto.getNome())
                .diaTreino(dto.getDiaTreino())
                .divisao(dto.getDivisao())
                .professor(professor)
                .aluno(aluno)
                .ativo(true)
                .build();

        List<TreinoExercicio> exercicios = dto.getExercicios().stream()
                .map(e -> {
                    Exercicio exercicio = exercicioRepository.findById(e.getExercicioId())
                            .orElseThrow(() -> new RuntimeException("Exercício não encontrado"));

                    return TreinoExercicio.builder()
                            .treino(treino)
                            .exercicio(exercicio)
                            .series(e.getSeries())
                            .repeticoes(e.getRepeticoes())
                            .descanso(e.getDescanso())
                            .observacao(e.getObservacao())
                            .ordem(e.getOrdem())
                            .build();
                })
                .collect(Collectors.toList());

        treino.setExercicios(exercicios);
        return toDTO(treinoRepository.save(treino));
    }

    public List<TreinoResponseDTO> listarPorAluno(Long alunoId) {
        return treinoRepository.findByAlunoIdComExercicios(alunoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TreinoResponseDTO> listarPorProfessor(Long professorId) {
        return treinoRepository.findByProfessorIdComExercicios(professorId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public TreinoResponseDTO buscarPorId(Long id) {
        return toDTO(treinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado")));
    }

    public TreinoResponseDTO editar(Long id, String emailProfessor, TreinoCreateDTO dto) {
        Treino treino = treinoRepository.findByIdComExercicios(id)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado"));

        User usuario = userRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.name().equals("ROLE_ADMIN"));

        if (!isAdmin && !treino.getProfessor().getEmail().equals(emailProfessor)) {
            throw new RuntimeException("Você não tem permissão para editar este treino");
        }

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        treino.setNome(dto.getNome());
        treino.setDiaTreino(dto.getDiaTreino());
        treino.setDivisao(dto.getDivisao());
        treino.setAluno(aluno);

        treino.getExercicios().clear();

        List<TreinoExercicio> novosExercicios = dto.getExercicios().stream()
                .map(e -> {
                    Exercicio exercicio = exercicioRepository.findById(e.getExercicioId())
                            .orElseThrow(() -> new RuntimeException("Exercício não encontrado"));

                    return TreinoExercicio.builder()
                            .treino(treino)
                            .exercicio(exercicio)
                            .series(e.getSeries())
                            .repeticoes(e.getRepeticoes())
                            .descanso(e.getDescanso())
                            .observacao(e.getObservacao())
                            .ordem(e.getOrdem())
                            .build();
                })
                .collect(Collectors.toList());

        treino.getExercicios().addAll(novosExercicios);

        return toDTO(treinoRepository.save(treino));
    }

    public void desativar(Long id) {
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado"));
        treino.setAtivo(false);
        treinoRepository.save(treino);
    }

    private TreinoResponseDTO toDTO(Treino treino) {
        List<TreinoExercicioResponseDTO> exercicios = treino.getExercicios()
                .stream()
                .map(te -> new TreinoExercicioResponseDTO(
                        te.getId(),
                        te.getExercicio().getId(),
                        te.getExercicio().getNome(),
                        te.getExercicio().getGrupoMuscular(),
                        te.getExercicio().getGifUrl(),
                        te.getSeries(),
                        te.getRepeticoes(),
                        te.getDescanso(),
                        te.getObservacao(),
                        te.getOrdem()
                ))
                .sorted((a, b) -> {
                    if (a.getOrdem() == null) return 1;
                    if (b.getOrdem() == null) return -1;
                    return a.getOrdem().compareTo(b.getOrdem());
                })
                .toList();

        return new TreinoResponseDTO(
                treino.getId(),
                treino.getNome(),
                treino.getDiaTreino(),
                treino.getDivisao(),
                treino.getAluno().getNome(),
                treino.getProfessor().getNome(),
                exercicios,
                treino.getDataCriacao(),
                treino.isAtivo()
        );
    }

    public List<TreinoResponseDTO> listarPorAlunoEmail(String email) {
        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return listarPorAluno(aluno.getId());
    }

    public List<TreinoResponseDTO> listarPorProfessorEmail(String email) {
        User professor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        return listarPorProfessor(professor.getId());
    }
}