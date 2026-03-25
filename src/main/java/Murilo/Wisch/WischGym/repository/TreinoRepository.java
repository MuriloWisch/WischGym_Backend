package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByAlunoIdAndAtivoTrue(Long alunoId);
    List<Treino> findByProfessorIdAndAtivoTrue(Long professorId);

}