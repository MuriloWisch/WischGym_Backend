package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.treino.TreinoCreateDTO;
import Murilo.Wisch.WischGym.dto.treino.TreinoResponseDTO;
import Murilo.Wisch.WischGym.service.TreinoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treinos")
@RequiredArgsConstructor
public class TreinoController {

    private final TreinoService treinoService;

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TreinoResponseDTO> criar(
            @RequestBody @Valid TreinoCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(treinoService.criar(userDetails.getUsername(), dto));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<TreinoResponseDTO>> meusTreinos(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(treinoService.listarPorAlunoEmail(userDetails.getUsername()));
    }

    @GetMapping("/professor")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<TreinoResponseDTO>> treinosDoProfessor(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(treinoService.listarPorProfessorEmail(userDetails.getUsername()));
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<TreinoResponseDTO>> treinosDoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(treinoService.listarPorAluno(alunoId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<TreinoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        treinoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}