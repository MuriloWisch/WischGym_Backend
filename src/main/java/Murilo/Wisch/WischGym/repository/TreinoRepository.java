package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    @Query("""
        SELECT DISTINCT t FROM Treino t
        LEFT JOIN FETCH t.exercicios te
        LEFT JOIN FETCH te.exercicio
        WHERE t.aluno.id = :alunoId AND t.ativo = true
    """)
    List<Treino> findByAlunoIdComExercicios(Long alunoId);

    @Query("""
        SELECT DISTINCT t FROM Treino t
        LEFT JOIN FETCH t.exercicios te
        LEFT JOIN FETCH te.exercicio
        WHERE t.professor.id = :professorId AND t.ativo = true
    """)
    List<Treino> findByProfessorIdComExercicios(Long professorId);

}