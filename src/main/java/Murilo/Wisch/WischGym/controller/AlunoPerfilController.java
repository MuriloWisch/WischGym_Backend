package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.aluno.AlunoFotoRequest;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilResponse;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilUpdateRequest;
import Murilo.Wisch.WischGym.service.AlunoPerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aluno/perfil")
@RequiredArgsConstructor
public class AlunoPerfilController {

    private final AlunoPerfilService alunoPerfilService;

    @GetMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AlunoPerfilResponse> getPerfil() {
        return ResponseEntity.ok(alunoPerfilService.getPerfil());
    }

    @PutMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AlunoPerfilResponse> updatePerfil(
            @Valid @RequestBody AlunoPerfilUpdateRequest request
    ) {
        return ResponseEntity.ok(alunoPerfilService.updatePerfil(request));
    }

    @PostMapping("/foto")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Void> updateFoto(
            @Valid @RequestBody AlunoFotoRequest request
    ) {
        alunoPerfilService.updateFoto(request);
        return ResponseEntity.noContent().build();
    }
}
