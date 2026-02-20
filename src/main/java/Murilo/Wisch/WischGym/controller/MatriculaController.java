package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.dto.matricula.MatriculaCreateDTO;
import Murilo.Wisch.WischGym.service.MatriculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Matricula> matricular(@RequestBody MatriculaCreateDTO dto){
        return ResponseEntity.ok(matriculaService.matricular(dto));
    }
}
