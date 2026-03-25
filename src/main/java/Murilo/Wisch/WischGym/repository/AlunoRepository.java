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
import java.util.List;
import java.util.Optional;


public interface AlunoRepository extends JpaRepository<Aluno, Long>, JpaSpecificationExecutor<Aluno> {

    long countByDataCadastroBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(StatusAlunos status);

    long countByAtivoTrue();

    Optional<Aluno> findByUserId(Long userId);

    long countByInadimplenteTrue();

    Page<Aluno> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Aluno> findByProfessorId(Long professorId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.ativo = true AND a.status = :status")
    long countActiveByFlagOrStatus(@Param("status") StatusAlunos status);

    Optional<Aluno> findByEmail(String email);

    @Query("SELECT a FROM Aluno a WHERE a.professor IS NULL AND a.status = 'ATIVO'")
    List<Aluno> findAlunosSemProfessor();

}
