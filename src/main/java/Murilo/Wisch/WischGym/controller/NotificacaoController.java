package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.NotificacaoResponse;
import Murilo.Wisch.WischGym.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponse>> getMinhas() {
        return ResponseEntity.ok(notificacaoService.getMinhas());
    }

    @GetMapping("/badge")
    public ResponseEntity<Map<String, Long>> getBadge() {
        return ResponseEntity.ok(Map.of("total", notificacaoService.contarNaoLidas()));
    }

    @PutMapping("/ler-todas")
    public ResponseEntity<Void> lerTodas() {
        notificacaoService.marcarTodasComoLidas();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ler")
    public ResponseEntity<Void> ler(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }
}
