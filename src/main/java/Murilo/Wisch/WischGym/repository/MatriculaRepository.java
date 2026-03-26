package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    boolean existsByAlunoIdAndStatus(Long alunoId, StatusMatricula status);
    List<Matricula> findByStatus(StatusMatricula status);
    
    List<Matricula> findByAlunoId(Long alunoId);
}
