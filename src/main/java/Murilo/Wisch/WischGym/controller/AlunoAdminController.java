import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.dto.aluno.AlunoCreateDTO;
import Murilo.Wisch.WischGym.dto.aluno.AlunoResponseDTO;
import Murilo.Wisch.WischGym.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/alunos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AlunoAdminController {

    private final AlunoService alunoService;

    @PostMapping
    public AlunoResponseDTO criar(@RequestBody @Valid AlunoCreateDTO dto){
        return alunoService.criar(dto);
    }

    @GetMapping
    public Page<Aluno> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String status,
            Pageable pageable
    ){
        return alunoService.listar(nome, status, pageable);
    }

    @GetMapping("/{id}")
    public AlunoResponseDTO buscar(@PathVariable Long id){
        return alunoService.buscarPorId(id);
    }
}