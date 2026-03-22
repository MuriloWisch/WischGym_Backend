package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class FinanceiroService {

    private final PagamentoRepository pagamentoRepository;
    private final AlunoRepository alunoRepository;

    public FinanceiroService(PagamentoRepository pagamentoRepository, AlunoRepository alunoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.alunoRepository = alunoRepository;
    }

    public DashBoardDTO dashBoard() {
        BigDecimal recebido = pagamentoRepository.totalRecebido();
        BigDecimal pendente = pagamentoRepository.totalPendente();
        BigDecimal atrasado = pagamentoRepository.totalAtrasado();
        BigDecimal receitaMes = pagamentoRepository.receitaMes();

        YearMonth ym = YearMonth.now();
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        long totalAlunos = alunoRepository.count();
        long totalAtivos = alunoRepository.countByStatus(StatusAlunos.ATIVO);
        long totalInativos = alunoRepository.countByStatus(StatusAlunos.INATIVO);
        long totalInadimplentes = alunoRepository.countByInadimplenteTrue();
        long novosNoMes = alunoRepository.countByDataCadastroBetween(start, end);

        return new DashBoardDTO(
                recebido, pendente, atrasado, receitaMes,
                totalAlunos, totalAtivos, totalInativos, totalInadimplentes, novosNoMes
        );
    }
}
