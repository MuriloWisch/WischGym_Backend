package Murilo.Wisch.WischGym.dto;

import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id,
        TipoNotificacao tipo,
        String titulo,
        String mensagem,
        boolean lida,
        LocalDateTime criadaEm
) {}
