package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.Notificacao;
import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;
import Murilo.Wisch.WischGym.dto.NotificacaoResponse;
import Murilo.Wisch.WischGym.repository.NotificacaoRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UserRepository userRepository;

    public void criar(User destinatario, TipoNotificacao tipo, String titulo, String mensagem) {
        Notificacao n = Notificacao.builder()
                .destinatario(destinatario)
                .tipo(tipo)
                .titulo(titulo)
                .mensagem(mensagem)
                .lida(false)
                .build();
        notificacaoRepository.save(n);
    }


    public List<NotificacaoResponse> getMinhas() {
        User user = getUserLogado();
        return notificacaoRepository
                .findByDestinatarioIdOrderByCriadaEmDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public long contarNaoLidas() {
        return notificacaoRepository
                .countByDestinatarioIdAndLidaFalse(getUserLogado().getId());
    }

    @Transactional
    public void marcarTodasComoLidas() {
        notificacaoRepository.marcarTodasComoLidas(getUserLogado().getId());
    }

    @Transactional
    public void marcarComoLida(Long id) {
        Notificacao n = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        n.setLida(true);
        notificacaoRepository.save(n);
    }

    private User getUserLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private NotificacaoResponse toResponse(Notificacao n) {
        return new NotificacaoResponse(
                n.getId(),
                n.getTipo(),
                n.getTitulo(),
                n.getMensagem(),
                n.isLida(),
                n.getCriadaEm()
        );
    }
}
