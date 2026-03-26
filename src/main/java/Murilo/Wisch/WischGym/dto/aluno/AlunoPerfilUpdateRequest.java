package Murilo.Wisch.WischGym.dto.aluno;


import Murilo.Wisch.WischGym.domain.enums.ObjetivoAluno;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record AlunoPerfilUpdateRequest(

        @DecimalMin("20.0") @DecimalMax("300.0")
        Double peso,

        @DecimalMin("1.0") @DecimalMax("2.5")
        Double altura,

        ObjetivoAluno objetivo
) {}
