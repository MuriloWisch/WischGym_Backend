package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.TreinoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TreinoLogRepository extends JpaRepository<TreinoLog, Long> {

    List<TreinoLog> findByAlunoIdOrderByDataDesc(Long alunoId);

    Optional<TreinoLog> findByAlunoIdAndTreinoIdAndData(Long alunoId, Long treinoId, LocalDate data);

    @Query("SELECT COUNT(t) FROM TreinoLog t WHERE t.aluno.id = :alunoId AND t.data BETWEEN :inicio AND :fim AND t.concluido = true")
    long countTreinosConcluidos(@Param("alunoId") Long alunoId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    List<TreinoLog> findByAlunoIdAndDataBetween(Long alunoId, LocalDate inicio, LocalDate fim);


    @Query("""
    SELECT l FROM TreinoLog l
    JOIN FETCH l.treino t
    WHERE l.aluno.id = :alunoId
    ORDER BY l.data DESC
""")
    List<TreinoLog> buscarHistoricoComTreino(@Param("alunoId") Long alunoId);
}