package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;

import java.time.LocalDate;

public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }
    public Pagamento pagar(Long id, PagamentoDTO dto) {
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pagamento não foi encontrado"));

        if (pagamento.getStatus() == StatusPagamento.PAGO){
            throw new RuntimeException("Este pagamento ja foi realizado");
        }

        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setDataPagamento(LocalDate.now());
        pagamento.setFormaPagamento(dto.getFormaPagamento());

        return pagamentoRepository.save(pagamento);
    }

}
