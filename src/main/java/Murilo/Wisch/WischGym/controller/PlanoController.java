package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.service.PlanoService;
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
    public ResponseEntity<Plano> criar(@RequestBody Plano plano){
        return ResponseEntity.ok(planoService.criar(plano));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<List<Plano>> listar(){
        return ResponseEntity.ok(planoService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<Plano> buscar(@PathVariable Long id){
        return ResponseEntity.ok(planoService.buscarPorid(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasHole('ADMIN')")
    public ResponseEntity<Plano> atualizar(@PathVariable Long id, @RequestBody Plano plano){
        return ResponseEntity.ok(planoService.atualizar(id, plano));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasHole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        planoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
