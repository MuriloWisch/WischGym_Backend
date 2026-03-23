package Murilo.Wisch.WischGym.repository;

import Murilo.Wisch.WischGym.domain.entities.Convite;
import Murilo.Wisch.WischGym.domain.enums.StatusConvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConviteRepository extends JpaRepository<Convite, Long> {

    List<Convite> findByAlunoIdOrderByDataCriacaoDesc(Long alunoId);
    List<Convite> findByProfessorIdOrderByDataCriacaoDesc(Long professorId);
    boolean existsByProfessorIdAndAlunoIdAndStatus(Long professorId, Long alunoId, StatusConvite status);

}
