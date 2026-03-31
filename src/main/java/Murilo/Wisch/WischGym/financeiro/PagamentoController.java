package Murilo.Wisch.WischGym.financeiro;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoService pagamentoService;

    @PutMapping("/{id}/pagar")
    @PreAuthorize("HasRole('ADMIN')")
    public ResponseEntity<Pagamento> pagar(@PathVariable Long id, @RequestBody PagamentoDTO dto){
        return ResponseEntity.ok(pagamentoService.pagar(id, dto));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<PagamentoResponse>> getMeus() {
        return ResponseEntity.ok(pagamentoService.getMeusPagamentos());
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PagamentoResponse>> getTodos() {
        return ResponseEntity.ok(pagamentoService.getTodosPagamentos());
    }
}
