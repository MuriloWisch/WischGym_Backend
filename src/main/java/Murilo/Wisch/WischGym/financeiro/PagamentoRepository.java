package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByMatriculaId(Long matriculaId);

    List<Pagamento> findByStatus(StatusPagamento status);

    boolean existsByMatriculaIdAndStatus(Long matriculaId, StatusPagamento status);

    List<Pagamento> findByDataVencimentoBeforeAndStatus(LocalDate data, StatusPagamento status);

}
