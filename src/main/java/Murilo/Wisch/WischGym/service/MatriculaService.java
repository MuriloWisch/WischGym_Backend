package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.dto.matricula.MatriculaCreateDTO;
import Murilo.Wisch.WischGym.financeiro.Pagamento;
import Murilo.Wisch.WischGym.financeiro.PagamentoRepository;
import Murilo.Wisch.WischGym.financeiro.enums.StatusPagamento;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.repository.PlanoRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final PagamentoRepository pagamentoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository, AlunoRepository alunoRepository, PlanoRepository planoRepository, PagamentoRepository pagamentoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public Matricula matricular(MatriculaCreateDTO dto){

        Aluno aluno = alunoRepository.findById(dto.getAlunoId()).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        Plano plano = planoRepository.findById(dto.getPlanoId()).orElseThrow(() -> new RuntimeException("Matricula não encontrado."));

        Optional<Matricula> matriculaAtiva = matriculaRepository.findByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA);

        if (matriculaAtiva.isPresent()) {
            throw new RuntimeException("Aluno ja possui uma matricula ativa");
        }

        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusMonths(plano.getDuracaoMeses());

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setDataInicio(dataInicio);
        matricula.setDataFim(dataFim);
        matricula.setStatus(StatusMatricula.ATIVA);
        matricula.setValor(plano.getValor());
        matricula.setProximoPagamento(LocalDate.now().plusMonths(1));

        Matricula matriculaSalva = matriculaRepository.save(matricula);


        Pagamento pagamento = new Pagamento();
        pagamento.setMatricula(matriculaSalva);
        pagamento.setValor(plano.getValor());
        pagamento.setDataVencimento(dataInicio.plusMonths(1));
        pagamento.setStatus(StatusPagamento.PENDENTE);

        pagamentoRepository.save(pagamento);

        return matriculaSalva;
    }

    public Matricula buscarPorId(Long id){
        Matricula matricula = matriculaRepository.findById(id).orElseThrow(() -> new RuntimeException("Matricula Não encontrada"));

        atualizarStatusSeNecessario(matricula);
        return matricula;
    }

    private void atualizarStatusSeNecessario(Matricula matricula) {
        if (matricula.getStatus() == StatusMatricula.ATIVA && matricula.getDataFim().isBefore(LocalDate.now())){

            matricula.setStatus(StatusMatricula.VENCIDA);
            matriculaRepository.save(matricula);
            atualizarStatusAluno(matricula.getAluno());
        }
    }

    public Matricula renovar(Long id){
        Matricula matricula = matriculaRepository.findById(id).orElseThrow(() -> new RuntimeException("Matricula não encontrada"));

        Plano plano = matricula.getPlano();

        Aluno aluno = matricula.getAluno();

        if(aluno.isInadimplente()){
            throw new RuntimeException("Aluno inadimplente. Regularize os pagamentos.");
        }

        if (matricula.getStatus() == StatusMatricula.CANCELADA){
            throw new RuntimeException("Não é possivel renovar uma matricula ja cancelada");
        }

        LocalDate hoje = LocalDate.now();

        if (matricula.getStatus() == StatusMatricula.ATIVA && matricula.getDataFim().isAfter(hoje)){
            matricula.setDataFim(matricula.getDataFim().plusMonths(plano.getDuracaoMeses()));
        } else {
            matricula.setDataInicio(hoje);
            matricula.setDataFim(hoje.plusMonths(plano.getDuracaoMeses()));
        }
        matricula.setStatus(StatusMatricula.ATIVA);
        return matriculaRepository.save(matricula);

    }

    public Matricula cancelar(Long id) {
        Matricula matricula = matriculaRepository.findById(id).orElseThrow(() -> new RuntimeException("Matricula não encontrada"));

        if (matricula.getStatus() == StatusMatricula.CANCELADA){
            throw new RuntimeException("Esta matricula ja esta cancela");
        }

        matricula.setStatus(StatusMatricula.CANCELADA);
        Matricula matriculaSalva = matriculaRepository.save(matricula);

        atualizarStatusAluno(matricula.getAluno());
        return matriculaSalva;
    }


    private void atualizarStatusAluno(Aluno aluno) {
        boolean possuiMatriculaAtiva =
                matriculaRepository.existsByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA);

        boolean possuiPagamentoAtrasado = possuiMatriculaAtiva &&
                pagamentoRepository.existsByMatriculaIdAndStatus(
                        matriculaRepository.findByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA)
                                .map(Matricula::getId).orElse(-1L),
                        StatusPagamento.ATRASADO
                );

        if (!possuiMatriculaAtiva) {
            aluno.setStatus(StatusAlunos.INATIVO);
            aluno.setAtivo(false);
            aluno.setInadimplente(false);
        } else if (possuiPagamentoAtrasado) {
            aluno.setStatus(StatusAlunos.INADIMPLENTE);
            aluno.setAtivo(true);
            aluno.setInadimplente(true);
        } else {
            aluno.setStatus(StatusAlunos.ATIVO);
            aluno.setAtivo(true);
            aluno.setInadimplente(false);
        }
        alunoRepository.save(aluno);
    }

    public List<Matricula> listarPorStatus(StatusMatricula status){
        return matriculaRepository.findByStatus(status);
    }

    public Matricula buscarAtivaPorAluno(Long alunoId){
        return matriculaRepository.findByAlunoIdAndStatus(alunoId, StatusMatricula.ATIVA)
                .orElseThrow(() -> new RuntimeException("Aluno não possui matricula ativa"));
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void atualizarMatriculasVencidas(){
     List<Matricula> matriculasAtivas = matriculaRepository.findByStatus(StatusMatricula.ATIVA);

     LocalDate hoje = LocalDate.now();

     for (Matricula matricula : matriculasAtivas){

         if (matricula.getDataFim().isBefore(hoje)){

             matricula.setStatus(StatusMatricula.VENCIDA);
             matriculaRepository.save(matricula);

             atualizarStatusAluno(matricula.getAluno());
         }
      }
    }
}
