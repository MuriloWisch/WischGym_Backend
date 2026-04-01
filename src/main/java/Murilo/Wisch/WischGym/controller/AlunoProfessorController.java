package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professor/alunos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
public class AlunoProfessorController {

    private final AlunoService alunoService;

    @GetMapping
    public Page<AlunoResponseDTO> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return alunoService.listarPorProfessor(userDetails.getUsername(), nome, status, pageable);
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<AlunoResponseDTO>> alunosDisponiveis() {
        return ResponseEntity.ok(alunoService.listarSemProfessor());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<AlunoResponseDTO>> listarTodos(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(alunoService.listarTodosPorProfessor(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public AlunoResponseDTO buscar(@PathVariable Long id) {
        return alunoService.buscarPorId(id);
    }
}