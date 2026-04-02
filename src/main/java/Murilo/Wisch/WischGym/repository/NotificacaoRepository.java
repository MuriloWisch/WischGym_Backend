package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByDestinatarioIdOrderByCriadaEmDesc(Long userId);

    long countByDestinatarioIdAndLidaFalse(Long userId);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.destinatario.id = :userId")
    void marcarTodasComoLidas(Long userId);

    void deleteAllByDestinatario(User destinatario);

}
