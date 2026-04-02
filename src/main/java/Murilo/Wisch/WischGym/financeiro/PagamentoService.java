package Murilo.Wisch.WischGym.financeiro;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;
import Murilo.Wisch.WischGym.exception.PagamentoNaoEncontradoException;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import Murilo.Wisch.WischGym.repository.UserRepository;
import Murilo.Wisch.WischGym.service.NotificacaoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final NotificacaoService notificacaoService;
    private final UserRepository userRepository;



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

        Aluno aluno = pagamento.getMatricula().getAluno();
        if (aluno.getUser() != null) {
            notificacaoService.criar(
                    aluno.getUser(),
                    TipoNotificacao.PAGAMENTO_REALIZADO,
                    "Pagamento confirmado",
                    "Seu pagamento de R$ " + pagamento.getValor() + " referente ao plano " +
                            pagamento.getMatricula().getPlano().getNome() + " foi confirmado."
            );
        }

        userRepository.findByRolesContaining(Roles.ADMIN).forEach(admin ->
                notificacaoService.criar(
                        admin,
                        TipoNotificacao.PAGAMENTO_REALIZADO,
                        "Pagamento recebido",
                        "O aluno " + aluno.getNome() + " realizou o pagamento de R$ " +
                                pagamento.getValor() + "."
                )
        );
        return pagamento;
    }

    public List<PagamentoAlunoDTO> historicoAluno(Long alunoId){
        return pagamentoRepository.historicoFinanceiroAluno(alunoId);
    }

    public void verificarInadimplencia(Matricula matricula) {
        boolean possuiAtrasado = pagamentoRepository
                .existsByMatriculaIdAndStatus(matricula.getId(), StatusPagamento.ATRASADO);

        Aluno aluno = matricula.getAluno();

        if (possuiAtrasado) {
            aluno.setStatus(StatusAlunos.INADIMPLENTE);
            aluno.setAtivo(true);
            aluno.setInadimplente(true);
        } else {
            matricula.setStatus(StatusMatricula.ATIVA);
            aluno.setStatus(StatusAlunos.ATIVO);
            aluno.setAtivo(true);
            aluno.setInadimplente(false);
        }

        matriculaRepository.save(matricula);
        alunoRepository.save(aluno);
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

    public List<PagamentoResponse> getMeusPagamentos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Aluno aluno = alunoRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        return pagamentoRepository
                .findByMatricula_AlunoIdOrderByIdDesc(aluno.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PagamentoResponse> getTodosPagamentos() {
        return pagamentoRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PagamentoResponse toResponse(Pagamento p) {
        return new PagamentoResponse(
                p.getId(),
                p.getMatricula().getPlano().getNome(),
                p.getValor(),
                p.getStatus(),
                p.getDataPagamento(),
                p.getDataVencimento(),
                p.getMatricula().getDataInicio(),
                p.getMatricula().getDataFim(),
                p.getMatricula().getAluno().getNome()
        );
    }
}
