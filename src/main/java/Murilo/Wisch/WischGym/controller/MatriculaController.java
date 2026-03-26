package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.matricula.MatriculaDetalheResponse;
import Murilo.Wisch.WischGym.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matricula")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping("/minha")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> getMinhaMatricula() {
        return ResponseEntity.ok(matriculaService.getMinhaMatricula());
    }

    @PostMapping("/solicitar/{planoId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> solicitar(@PathVariable Long planoId) {
        return ResponseEntity.ok(matriculaService.solicitarMatricula(planoId));
    }

    @PostMapping("/pagar/{pagamentoId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> pagar(@PathVariable Long pagamentoId) {
        return ResponseEntity.ok(matriculaService.simularPagamento(pagamentoId));
    }

    @PostMapping("/renovar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> renovar() {
        return ResponseEntity.ok(matriculaService.renovarMatricula());
    }
}