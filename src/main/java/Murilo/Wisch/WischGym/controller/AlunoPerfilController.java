package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilResponse;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilUpdateRequest;
import Murilo.Wisch.WischGym.service.AlunoPerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Map<String, String>> updateFoto(
            @RequestParam("file") MultipartFile file
    ) {
        String url = alunoPerfilService.updateFoto(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
