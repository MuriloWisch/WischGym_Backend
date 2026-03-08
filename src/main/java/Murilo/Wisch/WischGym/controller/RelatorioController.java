package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.RelatorioMensalDTO;
import Murilo.Wisch.WischGym.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/mensal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RelatorioMensalDTO> relatorioMensal(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        if (month != null && (month < 1 || month > 12)) {
            return ResponseEntity.badRequest().build();
        }
        if (year != null && (year < 1900 || year > 3000)) {
            return ResponseEntity.badRequest().build();
        }
        RelatorioMensalDTO dto = relatorioService.gerarRelatorioMensal(year, month);
        return ResponseEntity.ok(dto);
    }
}
