package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;
import Murilo.Wisch.WischGym.dto.matricula.MatriculaDetalheResponse;
import Murilo.Wisch.WischGym.financeiro.Pagamento;
import Murilo.Wisch.WischGym.financeiro.PagamentoRepository;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.repository.PlanoRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final AlunoRepository     alunoRepository;
    private final PlanoRepository     planoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final NotificacaoService  notificacaoService;
    private final UserRepository userRepository;

    private Aluno getAlunoLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return alunoRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }


    public MatriculaDetalheResponse getMinhaMatricula() {
        Aluno aluno = getAlunoLogado();
        return matriculaRepository
                .findTopByAlunoIdOrderByDataInicioDesc(aluno.getId())
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public MatriculaDetalheResponse iniciarMatricula(Long planoId) {
        Aluno aluno = getAlunoLogado();
        Plano plano = planoRepository.findById(planoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plano não encontrado"));

        matriculaRepository.findTopByAlunoIdOrderByDataInicioDesc(aluno.getId())
                .ifPresent(m -> {
                    if (m.getStatus() == StatusMatricula.ATIVA)
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "Já possui matrícula ativa");
                });

        matriculaRepository
                .findByAlunoIdAndStatus(aluno.getId(), StatusMatricula.AGUARDANDO_PAGAMENTO)
                .forEach(m -> {
                    m.setStatus(StatusMatricula.CANCELADA);
                    matriculaRepository.save(m);
                });

        LocalDate hoje = LocalDate.now();

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setDataInicio(hoje);
        matricula.setDataFim(hoje.plusMonths(plano.getDuracaoMeses()));
        matricula.setProximoPagamento(hoje.plusMonths(1));
        matricula.setValor(plano.getValor());
        matricula.setStatus(StatusMatricula.AGUARDANDO_PAGAMENTO);

        matriculaRepository.save(matricula);
        return toResponse(matricula);
    }

    @Transactional
    public MatriculaDetalheResponse gerarPagamento(Long matriculaId) {
        Aluno aluno = getAlunoLogado();

        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matrícula não encontrada"));

        if (!matricula.getAluno().getId().equals(aluno.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if (matricula.getStatus() != StatusMatricula.AGUARDANDO_PAGAMENTO)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Matrícula não está aguardando pagamento");

        var pagamentoExistente = pagamentoRepository
                .findTopByMatriculaIdAndStatus(matriculaId, StatusPagamento.PENDENTE)
                .orElse(null);

        if (pagamentoExistente != null) {
            return toResponse(matricula);
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setMatricula(matricula);
        pagamento.setValor(matricula.getValor());
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setDataVencimento(LocalDate.now().plusDays(3));
        pagamentoRepository.save(pagamento);

        return toResponse(matricula);
    }


    @Transactional
    public MatriculaDetalheResponse confirmarPagamento(Long pagamentoId) {
        Aluno aluno = getAlunoLogado();

        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pagamento não encontrado"));

        if (!pagamento.getMatricula().getAluno().getId().equals(aluno.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if (pagamento.getStatus() != StatusPagamento.PENDENTE)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Pagamento não está pendente");

        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setDataPagamento(LocalDate.now());
        pagamentoRepository.save(pagamento);

        Matricula matricula = pagamento.getMatricula();
        matricula.setStatus(StatusMatricula.ATIVA);
        matriculaRepository.save(matricula);

        aluno.setStatus(StatusAlunos.ATIVO);
        alunoRepository.save(aluno);

        return toResponse(matricula);
    }


    @Transactional
    public MatriculaDetalheResponse renovarMatricula() {
        Aluno aluno = getAlunoLogado();

        Matricula ultima = matriculaRepository
                .findTopByAlunoIdOrderByDataInicioDesc(aluno.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nenhuma matrícula encontrada"));

        if (ultima.getStatus() == StatusMatricula.ATIVA)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Matrícula ainda está ativa");

        LocalDate hoje = LocalDate.now();

        Matricula nova = new Matricula();
        nova.setAluno(aluno);
        nova.setPlano(ultima.getPlano());
        nova.setDataInicio(hoje);
        nova.setDataFim(hoje.plusMonths(ultima.getPlano().getDuracaoMeses()));
        nova.setProximoPagamento(hoje.plusMonths(1));
        nova.setValor(ultima.getValor());
        nova.setStatus(StatusMatricula.AGUARDANDO_PAGAMENTO);

        matriculaRepository.save(nova);
        return toResponse(nova);
    }


    private MatriculaDetalheResponse toResponse(Matricula m) {
        var pagamento = pagamentoRepository
                .findTopByMatriculaIdOrderByIdDesc(m.getId())
                .orElse(null);

        return new MatriculaDetalheResponse(
                m.getId(),
                m.getPlano().getNome(),
                m.getPlano().getValor(),
                m.getPlano().getDuracaoMeses(),
                m.getDataInicio(),
                m.getDataFim(),
                m.getStatus(),
                pagamento != null && pagamento.getStatus() == StatusPagamento.PENDENTE
                        ? pagamento.getId() : null,
                pagamento != null ? pagamento.getStatus() : null,
                pagamento != null ? pagamento.getDataPagamento() : null
        );
    }

    @Transactional
    public MatriculaDetalheResponse assinarMatricula(Long planoId) {
        Aluno aluno = getAlunoLogado();
        Plano plano = planoRepository.findById(planoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plano não encontrado"));

        matriculaRepository.findTopByAlunoIdOrderByDataInicioDesc(aluno.getId())
                .ifPresent(m -> {
                    if (m.getStatus() == StatusMatricula.ATIVA)
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "Já possui matrícula ativa");
                });

        matriculaRepository
                .findByAlunoIdAndStatus(aluno.getId(), StatusMatricula.AGUARDANDO_PAGAMENTO)
                .forEach(m -> {
                    m.setStatus(StatusMatricula.CANCELADA);
                    matriculaRepository.save(m);
                });

        LocalDate hoje = LocalDate.now();

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setDataInicio(hoje);
        matricula.setDataFim(hoje.plusMonths(plano.getDuracaoMeses()));
        matricula.setProximoPagamento(hoje.plusMonths(1));
        matricula.setValor(plano.getValor());
        matricula.setStatus(StatusMatricula.ATIVA);
        matriculaRepository.save(matricula);

        Pagamento pagamento = new Pagamento();
        pagamento.setMatricula(matricula);
        pagamento.setValor(plano.getValor());
        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setDataPagamento(hoje);
        pagamento.setDataVencimento(hoje);
        pagamentoRepository.save(pagamento);

        aluno.setStatus(StatusAlunos.ATIVO);
        alunoRepository.save(aluno);


        userRepository.findByRolesContaining(Roles.ADMIN).forEach(admin ->
                notificacaoService.criar(
                        admin,
                        TipoNotificacao.PAGAMENTO_REALIZADO,
                        "Pagamento realizado",
                        "O aluno " + aluno.getNome() + " realizou o pagamento do plano " + plano.getNome() + "."
                )
        );

        notificacaoService.criar(
                aluno.getUser(),
                TipoNotificacao.MATRICULA_ATIVADA,
                "Matrícula ativada",
                "Sua matrícula no plano " + plano.getNome() + " foi ativada com sucesso. Bons treinos!"
        );

        return toResponse(matricula);

    }
}