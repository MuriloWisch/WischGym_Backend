package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.matricula.MatriculaDetalheResponse;
import Murilo.Wisch.WischGym.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/iniciar/{planoId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> iniciar(@PathVariable Long planoId) {
        return ResponseEntity.ok(matriculaService.iniciarMatricula(planoId));
    }

    @PostMapping("/gerar-pagamento/{matriculaId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> gerarPagamento(@PathVariable Long matriculaId) {
        return ResponseEntity.ok(matriculaService.gerarPagamento(matriculaId));
    }

    @PostMapping("/confirmar-pagamento/{pagamentoId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> confirmarPagamento(@PathVariable Long pagamentoId) {
        return ResponseEntity.ok(matriculaService.confirmarPagamento(pagamentoId));
    }

    @PostMapping("/renovar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> renovar() {
        return ResponseEntity.ok(matriculaService.renovarMatricula());
    }

    @PostMapping("/assinar/{planoId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<MatriculaDetalheResponse> assinar(@PathVariable Long planoId) {
        return ResponseEntity.ok(matriculaService.assinarMatricula(planoId));
    }
}