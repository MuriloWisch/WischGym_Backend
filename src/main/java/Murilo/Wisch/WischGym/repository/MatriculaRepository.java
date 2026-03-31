package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByAlunoIdAndStatus(Long alunoId, StatusMatricula status);
    List<Matricula> findByStatus(StatusMatricula status);

    Optional<Matricula> findTopByAlunoIdOrderByDataInicioDesc(Long alunoId);
    List<Matricula> findByAlunoId(Long alunoId);

    Optional<Matricula> findFirstByAlunoIdAndStatusOrderByDataInicioDesc(
            Long alunoId,
            StatusMatricula status
    );

    List<Matricula> findByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    @Query("SELECT m FROM Matricula m WHERE m.status = 'ATIVA' AND m.dataFim = :data")
    List<Matricula> findVencendoEm(@Param("data") LocalDate data);

    List<Matricula> findByStatusAndDataFimBefore(StatusMatricula status, LocalDate data);
}
