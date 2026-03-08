package Murilo.Wisch.WischGym.financeiro;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FinanceiroService {
    private final PagamentoRepository pagamentoRepository;

    public FinanceiroService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public DashBoardDTO dashBoard(){
        BigDecimal recebido = pagamentoRepository.totalRecebido();
        BigDecimal pendente = pagamentoRepository.totalPendente();
        BigDecimal atrasado = pagamentoRepository.totalAtrasado();
        BigDecimal receitaMes = pagamentoRepository.receitaMes();

        return new DashBoardDTO(recebido,pendente,atrasado,receitaMes);
    }
}
