package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final MatriculaRepository matriculaRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, MatriculaRepository matriculaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.matriculaRepository = matriculaRepository;
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void gerarMensalidadesAutomaticas(){
        List<Matricula> matriculasAtivas = matriculaRepository.findByStatus(StatusMatricula.ATIVA);

        LocalDate hoje = LocalDate.now();

        for (Matricula matricula : matriculasAtivas){
            LocalDate proximoVencimento = hoje.withDayOfMonth(matricula.getDataInicio().getDayOfMonth());

            if (proximoVencimento.isBefore(hoje)){
                proximoVencimento = proximoVencimento.plusMonths(1);
            }

            boolean existePagamento = pagamentoRepository.existsByMatriculaIdAndStatus(matricula.getId(),StatusPagamento.PENDENTE);

            if (!existePagamento){
                Pagamento pagamento = new Pagamento();
                pagamento.setMatricula(matricula);
                pagamento.setValor(matricula.getValor());
                pagamento.setDataVencimento(proximoVencimento);
                pagamento.setStatus(StatusPagamento.PENDENTE);

                pagamentoRepository.save(pagamento);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void atualizarPagamentosAtrasados() {

        List<Pagamento> pagamentosVencidos =
                pagamentoRepository
                        .findByDataVencimentoBeforeAndStatus(
                                LocalDate.now(),
                                StatusPagamento.PENDENTE
                        );

        for (Pagamento pagamento : pagamentosVencidos) {

            pagamento.setStatus(StatusPagamento.ATRASADO);
            pagamentoRepository.save(pagamento);
        }
    }
}
