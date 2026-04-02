package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.dto.plano.PlanoCreateDTO;
import Murilo.Wisch.WischGym.dto.plano.PlanoResponseDTO;
import Murilo.Wisch.WischGym.dto.plano.PlanoUpdateDTO;
import Murilo.Wisch.WischGym.service.PlanoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planos")
public class PlanoController {

    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanoResponseDTO> criar(@RequestBody @Valid PlanoCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(planoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<PlanoResponseDTO>> listar(){
        return ResponseEntity.ok(planoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> buscar(@PathVariable Long id){
        return ResponseEntity.ok(planoService.buscarPorid(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PlanoUpdateDTO dto){
        return ResponseEntity.ok(planoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        planoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}