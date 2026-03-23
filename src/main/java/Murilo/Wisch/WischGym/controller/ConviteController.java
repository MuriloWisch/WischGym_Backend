package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.convite.ConviteEnviarDTO;
import Murilo.Wisch.WischGym.dto.convite.ConviteResponseDTO;
import Murilo.Wisch.WischGym.service.ConviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/convites")
@RequiredArgsConstructor
public class ConviteController {

    private final ConviteService conviteService;

    @PostMapping("/enviar")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ConviteResponseDTO> enviar(
            @RequestBody @Valid ConviteEnviarDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(conviteService.enviar(userDetails.getUsername(), dto));
    }

    @GetMapping("/enviados")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<ConviteResponseDTO>> enviados(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(conviteService.listarEnviados(userDetails.getUsername()));
    }

    @GetMapping("/recebidos")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<ConviteResponseDTO>> recebidos(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(conviteService.listarRecebidos(userDetails.getUsername()));
    }

    @PutMapping("/{id}/aceitar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<ConviteResponseDTO> aceitar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(conviteService.aceitar(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}/recusar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<ConviteResponseDTO> recusar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(conviteService.recusar(id, userDetails.getUsername()));
    }
}
