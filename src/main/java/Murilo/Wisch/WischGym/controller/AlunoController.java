package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping
    public AlunoResponseDTO criar(@RequestBody @Valid AlunoCreateDTO dto){
        return alunoService.criar(dto);
    }

    @GetMapping
    public Page<AlunoResponseDTO> listar (Pageable pageable){
        return alunoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public AlunoResponseDTO buscar(@PathVariable Long id){
        return alunoService.buscarPorId(id);
    }
}
