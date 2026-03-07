package Murilo.Wisch.WischGym.handler;

import Murilo.Wisch.WischGym.exception.ErrorResponsee;
import Murilo.Wisch.WischGym.exception.PagamentoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
    public class GlobalExceptionHandler {

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponsee> handleNotFound(
            PagamentoNaoEncontradoException ex,
            HttpServletRequest request) {

        ErrorResponsee error = new ErrorResponsee(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponsee> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponsee error = new ErrorResponsee(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
