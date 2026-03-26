package Murilo.Wisch.WischGym.dto.aluno;

import Murilo.Wisch.WischGym.domain.enums.ObjetivoAluno;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;

public record AlunoPerfilResponse(

        Long id,

        String nome,

        String email,

        String fotoPerfil,

        Double peso,

        Double altura,

        ObjetivoAluno objetivo,

        StatusAlunos status,

        MatriculaResumoDTO matricula
) {}