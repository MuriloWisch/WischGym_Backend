package Murilo.Wisch.WischGym.dto.aluno;

import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;

import java.time.LocalDate;

public record MatriculaResumoDTO(

        Long id,

        String nomePlano,

        java.math.BigDecimal valorPlano,

        LocalDate dataInicio,

        LocalDate dataFim,

        StatusMatricula status

) {}
