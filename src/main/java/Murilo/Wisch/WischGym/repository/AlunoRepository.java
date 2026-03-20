package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface AlunoRepository extends JpaRepository<Aluno, Long>, JpaSpecificationExecutor<Aluno> {

    long countByDataCadastroBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(StatusAlunos status);

    long countByAtivoTrue();

    long countByInadimplenteTrue();

    Page<Aluno> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Aluno> findByProfessorId(Long professorId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.ativo = true OR a.status = :status")
    long countActiveByFlagOrStatus(@Param("status") StatusAlunos status);

}
