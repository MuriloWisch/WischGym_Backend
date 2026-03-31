package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;
import Murilo.Wisch.WischGym.financeiro.PagamentoRepository;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AgendadorService {

    private final MatriculaRepository matriculaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UserRepository userRepository;
    private final NotificacaoService   notificacaoService;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void executar() {
        notificarMatriculasVencendo();
        notificarPagamentosAtrasados();
        atualizarStatusMatriculas();
    }

    private void notificarMatriculasVencendo() {
        LocalDate em7dias = LocalDate.now().plusDays(7);
        matriculaRepository.findVencendoEm(em7dias).forEach(m ->
                notificacaoService.criar(
                        m.getAluno().getUser(),
                        TipoNotificacao.MATRICULA_VENCENDO,
                        "Matrícula vencendo",
                        "Sua matrícula no plano " + m.getPlano().getNome() + " vence em 7 dias."
                )
        );
    }

    private void notificarPagamentosAtrasados() {
        pagamentoRepository.findByStatus(StatusPagamento.ATRASADO).forEach(p ->
                userRepository.findByRolesContaining(Roles.ADMIN).forEach(admin ->
                        notificacaoService.criar(
                                admin,
                                TipoNotificacao.PAGAMENTO_ATRASADO,
                                "Pagamento atrasado",
                                "O aluno " + p.getMatricula().getAluno().getNome() +
                                        " está com pagamento atrasado de R$ " + p.getValor()
                        )
                )
        );
    }

    private void atualizarStatusMatriculas() {
        matriculaRepository
                .findByStatusAndDataFimBefore(StatusMatricula.ATIVA, LocalDate.now())
                .forEach(m -> {
                    m.setStatus(StatusMatricula.VENCIDA);
                    matriculaRepository.save(m);
                });
    }
}
