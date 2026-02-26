package Murilo.Wisch.WischGym.financeiro;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PutMapping("/{id}/pagar")
    @PreAuthorize("HasRole('ADMIN')")
    public ResponseEntity<Pagamento> pagar(@PathVariable Long id, @RequestBody PagamentoDTO dto){
        return ResponseEntity.ok(pagamentoService.pagar(id, dto));
    }
}
