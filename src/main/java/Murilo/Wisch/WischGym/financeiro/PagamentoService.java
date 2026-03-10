package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.exception.PagamentoNaoEncontradoException;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;


    public PagamentoService(PagamentoRepository pagamentoRepository, MatriculaRepository matriculaRepository, AlunoRepository alunoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
    }

    public Pagamento pagar(Long id, PagamentoDTO dto) {
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(()
                -> new PagamentoNaoEncontradoException("Pagamento com id " + id +  " não foi encontrado"));

        if (pagamento.getStatus() == StatusPagamento.PAGO){
            throw new RuntimeException("Este pagamento ja foi realizado");
        }

        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setDataPagamento(LocalDate.now());
        pagamento.setFormaPagamento(dto.getFormaPagamento());

        pagamentoRepository.save(pagamento);
        verificarInadimplencia(pagamento.getMatricula());

        return pagamento;
    }

    public List<PagamentoAlunoDTO> historicoAluno(Long alunoId){
        return pagamentoRepository.historicoFinanceiroAluno(alunoId);
    }

    public void verificarInadimplencia(Matricula matricula){

        boolean possuiAtrasado = pagamentoRepository
                .existsByMatriculaIdAndStatus(matricula.getId(), StatusPagamento.ATRASADO);

        Aluno aluno = matricula.getAluno();

        if(possuiAtrasado){
            matricula.setStatus(StatusMatricula.VENCIDA);
            aluno.setAtivo(false);
            aluno.setStatus(StatusAlunos.INADIMPLENTE);
            aluno.setInadimplente(true);
        }else{
            matricula.setStatus(StatusMatricula.ATIVA);
            aluno.setAtivo(true);
            aluno.setStatus(StatusAlunos.ATIVO);
            aluno.setInadimplente(false);
        }

        matriculaRepository.save(matricula);
        alunoRepository.save(aluno);
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

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void atualizarPagamentosAtrasados() {

        List<Pagamento> pagamentosVencidos =
                pagamentoRepository
                        .findByDataVencimentoBeforeAndStatus(
                                LocalDate.now(),
                                StatusPagamento.PENDENTE
                        );

        for (Pagamento pagamento : pagamentosVencidos) {

            pagamento.setStatus(StatusPagamento.ATRASADO);

            verificarInadimplencia(pagamento.getMatricula());

        }

        pagamentoRepository.saveAll(pagamentosVencidos);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void gerarCobrancasMensais(){
        List<Matricula> matriculas = matriculaRepository.findByStatus(StatusMatricula.ATIVA);

        for (Matricula matricula : matriculas) {
            if (matricula.getProximoPagamento().isBefore(LocalDate.now())
                    || matricula.getProximoPagamento().isEqual(LocalDate.now())){
                Pagamento pagamento = new Pagamento();

                pagamento.setMatricula(matricula);
                pagamento.setStatus(StatusPagamento.PENDENTE);
                pagamento.setValor(matricula.getPlano().getValor());
                pagamento.setDataVencimento(matricula.getProximoPagamento());

                pagamentoRepository.save(pagamento);

                matricula.setProximoPagamento(matricula.getProximoPagamento().plusMonths(1));
                matriculaRepository.save(matricula);
            }
        }
    }
}
