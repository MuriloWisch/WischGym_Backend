package Murilo.Wisch.WischGym.financeiro;


import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByMatriculaId(Long matriculaId);

    List<Pagamento> findByStatus(StatusPagamento status);

    Optional<Pagamento> findTopByMatriculaIdOrderByIdDesc(Long matriculaId);

    List<Pagamento> findByMatricula_Aluno_IdAndStatus(Long alunoId, StatusPagamento status);

    boolean existsByMatriculaIdAndStatus(Long matriculaId, StatusPagamento status);

    List<Pagamento> findByDataVencimentoBeforeAndStatus(LocalDate data, StatusPagamento status);

    @Query("SELECT COALESCE(SUM(p.valor),0) FROM Pagamento p WHERE p.status = 'PAGO'")
    BigDecimal totalRecebido();

    @Query("SELECT COALESCE(SUM(p.valor),0) FROM Pagamento p WHERE p.status = 'PENDENTE'")
    BigDecimal totalPendente();

    @Query("SELECT COALESCE(SUM(p.valor),0) FROM Pagamento p WHERE p.status = 'ATRASADO'")
    BigDecimal totalAtrasado();

    @Query("SELECT COALESCE(SUM(p.valor),0) FROM Pagamento p WHERE p.status = 'PAGO' AND MONTH(p.dataPagamento) = MONTH(CURRENT_DATE) AND YEAR(p.dataPagamento) = YEAR(CURRENT_DATE)")
    BigDecimal receitaMes();

    @Query("""
SELECT new Murilo.Wisch.WischGym.financeiro.PagamentoAlunoDTO(
    p.id,
    p.valor,
    p.status,
    p.dataVencimento,
    p.dataPagamento
)
FROM Pagamento p
WHERE p.matricula.aluno.id = :alunoId
ORDER BY p.dataVencimento DESC
""")
    List<PagamentoAlunoDTO> historicoFinanceiroAluno(Long alunoId);

    Optional<Pagamento> findFirstByMatriculaAlunoIdAndStatusOrderByIdDesc(
            Long alunoId,
            StatusPagamento status
    );

    Optional<Pagamento> findTopByMatriculaIdAndStatus(Long matriculaId, StatusPagamento status);
}
