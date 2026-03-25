package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.treino.ProgressoDTO;
import Murilo.Wisch.WischGym.dto.treino.RegistrarLogDTO;
import Murilo.Wisch.WischGym.service.TreinoLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/treinos/log")
@RequiredArgsConstructor
public class TreinoLogController {

    private final TreinoLogService treinoLogService;

    @PostMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<?> registrar(
            @RequestBody RegistrarLogDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(treinoLogService.registrarOuAtualizar(userDetails.getUsername(), dto));
    }

    @GetMapping("/historico")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<?> historico(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(treinoLogService.historico(userDetails.getUsername()));
    }

    @GetMapping("/progresso")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<ProgressoDTO> progresso(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(treinoLogService.progresso(userDetails.getUsername()));
    }
}