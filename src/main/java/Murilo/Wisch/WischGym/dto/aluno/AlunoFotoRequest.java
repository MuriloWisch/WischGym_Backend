package Murilo.Wisch.WischGym.dto.aluno;

import jakarta.validation.constraints.NotBlank;

public record AlunoFotoRequest(

        @NotBlank String fotoPerfil

) {}
