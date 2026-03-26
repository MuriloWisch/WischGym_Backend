package Murilo.Wisch.WischGym.dto.aluno;

import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;

import java.time.LocalDate;

public record MatriculaResumoDTO(

        Long id,

        String nomePlano,

        Double valorPlano,

        LocalDate dataInicio,

        LocalDate dataVencimento,

        StatusMatricula status

) {}
