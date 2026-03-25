package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.entities.Exercicio;
import Murilo.Wisch.WischGym.domain.enums.GrupoMuscular;
import Murilo.Wisch.WischGym.dto.treino.ExercicioDTO;
import Murilo.Wisch.WischGym.repository.ExercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;

    public Exercicio criar(ExercicioDTO dto) {
        Exercicio exercicio = Exercicio.builder()
                .nome(dto.getNome())
                .grupoMuscular(dto.getGrupoMuscular())
                .gifUrl(dto.getGifUrl())
                .descricao(dto.getDescricao())
                .build();
        return exercicioRepository.save(exercicio);
    }

    public List<Exercicio> listarTodos() {
        return exercicioRepository.findAll();
    }

    public List<Exercicio> listarPorGrupo(GrupoMuscular grupo) {
        return exercicioRepository.findByGrupoMuscular(grupo);
    }

    public List<Exercicio> buscarPorNome(String nome) {
        return exercicioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Exercicio buscarPorId(Long id) {
        return exercicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado"));
    }

    public Exercicio atualizar(Long id, ExercicioDTO dto) {
        Exercicio exercicio = buscarPorId(id);
        exercicio.setNome(dto.getNome());
        exercicio.setGrupoMuscular(dto.getGrupoMuscular());
        exercicio.setGifUrl(dto.getGifUrl());
        exercicio.setDescricao(dto.getDescricao());
        return exercicioRepository.save(exercicio);
    }

    public void deletar(Long id) {
        exercicioRepository.deleteById(id);
    }
}