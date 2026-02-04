package Murilo.Wisch.WischGym.dto.aluno;

import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AlunoResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private StatusAlunos status;
    private LocalDateTime dataCadastro;

}
