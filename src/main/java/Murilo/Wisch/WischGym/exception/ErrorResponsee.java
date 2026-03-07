package Murilo.Wisch.WischGym.exception;

import java.time.LocalDateTime;

public record ErrorResponsee(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
){}
