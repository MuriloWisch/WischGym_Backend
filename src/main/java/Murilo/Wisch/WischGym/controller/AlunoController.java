package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping
    public AlunoResponseDTO criar(@RequestBody @Valid AlunoCreateDTO dto){
        return alunoService.criar(dto);
    }

}
