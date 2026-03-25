package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.ExercicioLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExercicioLogRepository extends JpaRepository<ExercicioLog, Long> {

    List<ExercicioLog> findByTreinoLogId(Long treinoLogId);

}