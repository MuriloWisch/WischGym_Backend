package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.financeiro.PagamentoAlunoDTO;
import Murilo.Wisch.WischGym.financeiro.PagamentoService;
import Murilo.Wisch.WischGym.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;
    private final PagamentoService pagamentoService;

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

    @GetMapping("/{id}/pagamentos")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PagamentoAlunoDTO> historicoFinanceiro(@PathVariable Long id){
        return pagamentoService.historicoAluno(id);
    }

}
