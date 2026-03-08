package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    long countByDataCadastroBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(StatusAlunos status);
}
