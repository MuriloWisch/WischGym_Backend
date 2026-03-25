package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    @Query("""
        SELECT DISTINCT t FROM Treino t
        LEFT JOIN FETCH t.exercicios te
        LEFT JOIN FETCH te.exercicio
        WHERE t.aluno.id = :alunoId AND t.ativo = true
    """)
    List<Treino> findByAlunoIdComExercicios(@Param("alunoId") Long alunoId);

    @Query("""
        SELECT DISTINCT t FROM Treino t
        LEFT JOIN FETCH t.exercicios te
        LEFT JOIN FETCH te.exercicio
        WHERE t.professor.id = :professorId AND t.ativo = true
    """)
    List<Treino> findByProfessorIdComExercicios(@Param("professorId") Long professorId);

    @Query("""
    SELECT t FROM Treino t
    LEFT JOIN FETCH t.exercicios te
    LEFT JOIN FETCH te.exercicio
    WHERE t.id = :id
""")
    Optional<Treino> findByIdComExercicios(@Param("id") Long id);
}