package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.domain.entities.Exercicio;
import Murilo.Wisch.WischGym.domain.enums.GrupoMuscular;
import Murilo.Wisch.WischGym.dto.treino.ExercicioDTO;
import Murilo.Wisch.WischGym.service.ExercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercicios")
@RequiredArgsConstructor
public class ExercicioController {

    private final ExercicioService exercicioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<Exercicio> criar(@RequestBody @Valid ExercicioDTO dto) {
        return ResponseEntity.ok(exercicioService.criar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<Exercicio>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) GrupoMuscular grupo
    ) {
        if (nome != null) return ResponseEntity.ok(exercicioService.buscarPorNome(nome));
        if (grupo != null) return ResponseEntity.ok(exercicioService.listarPorGrupo(grupo));
        return ResponseEntity.ok(exercicioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<Exercicio> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(exercicioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<Exercicio> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ExercicioDTO dto
    ) {
        return ResponseEntity.ok(exercicioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        exercicioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}