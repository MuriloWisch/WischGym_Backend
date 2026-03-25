package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Exercicio;
import Murilo.Wisch.WischGym.domain.enums.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    List<Exercicio> findByGrupoMuscular(GrupoMuscular grupoMuscular);
    List<Exercicio> findByNomeContainingIgnoreCase(String nome);

}