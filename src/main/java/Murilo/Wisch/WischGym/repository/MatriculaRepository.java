package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    boolean existsByAlunoAndStatus(Long alunoid, StatusMatricula status);
}
