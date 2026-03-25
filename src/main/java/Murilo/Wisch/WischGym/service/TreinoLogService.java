package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.entities.*;
import Murilo.Wisch.WischGym.dto.treino.ProgressoDTO;
import Murilo.Wisch.WischGym.dto.treino.RegistrarLogDTO;
import Murilo.Wisch.WischGym.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreinoLogService {

    private final TreinoLogRepository treinoLogRepository;
    private final TreinoRepository treinoRepository;
    private final AlunoRepository alunoRepository;
    private final ExercicioLogRepository exercicioLogRepository;

    @Transactional
    public TreinoLog registrarOuAtualizar(String emailAluno, RegistrarLogDTO dto) {
        Aluno aluno = alunoRepository.findByEmail(emailAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Treino treino = treinoRepository.findByIdComExercicios(dto.getTreinoId())
                .orElseThrow(() -> new RuntimeException("Treino não encontrado"));

        TreinoLog log = treinoLogRepository
                .findByAlunoIdAndTreinoIdAndData(aluno.getId(), treino.getId(), LocalDate.now())
                .orElseGet(() -> {
                    TreinoLog novo = new TreinoLog();
                    novo.setAluno(aluno);
                    novo.setTreino(treino);
                    novo.setData(LocalDate.now());
                    return treinoLogRepository.save(novo);
                });

        exercicioLogRepository.deleteAll(
                exercicioLogRepository.findByTreinoLogId(log.getId())
        );

        int totalExercicios = treino.getExercicios().size();
        int concluidos = 0;

        for (TreinoExercicio te : treino.getExercicios()) {
            boolean feito = dto.getExerciciosConcluidos() != null &&
                    dto.getExerciciosConcluidos().contains(te.getId());

            ExercicioLog el = ExercicioLog.builder()
                    .treinoLog(log)
                    .treinoExercicio(te)
                    .concluido(feito)
                    .build();

            exercicioLogRepository.save(el);

            if (feito) concluidos++;
        }

        int porcentagem = totalExercicios > 0
                ? (int) ((concluidos * 100.0) / totalExercicios)
                : 0;

        if (porcentagem == 0) {
            throw new RuntimeException("Você precisa concluir pelo menos um exercício.");
        }
        log.setPorcentagemConcluida(porcentagem);
        log.setConcluido(porcentagem == 100);

        return treinoLogRepository.save(log);
    }

    public List<TreinoLog> historico(String emailAluno) {
        Aluno aluno = alunoRepository.findByEmail(emailAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return treinoLogRepository.findByAlunoIdOrderByDataDesc(aluno.getId());
    }

    public ProgressoDTO progresso(String emailAluno) {
        Aluno aluno = alunoRepository.findByEmail(emailAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(7);
        LocalDate inicioMes = hoje.withDayOfMonth(1);

        long treinosSemana = treinoLogRepository.countTreinosConcluidos(
                aluno.getId(), inicioSemana, hoje);

        long treinosMes = treinoLogRepository.countTreinosConcluidos(
                aluno.getId(), inicioMes, hoje);

        long treinosTotais = treinoLogRepository.countTreinosConcluidos(
                aluno.getId(), LocalDate.of(2000, 1, 1), hoje);

        List<TreinoLog> logs30dias = treinoLogRepository
                .findByAlunoIdAndDataBetween(aluno.getId(), hoje.minusDays(30), hoje);

        double mediaConsistencia = logs30dias.isEmpty() ? 0 :
                logs30dias.stream()
                        .mapToInt(TreinoLog::getPorcentagemConcluida)
                        .average()
                        .orElse(0);

        int sequencia = calcularSequencia(aluno.getId(), hoje);

        return new ProgressoDTO(treinosSemana, treinosMes, treinosTotais,
                mediaConsistencia, sequencia);
    }

    private int calcularSequencia(Long alunoId, LocalDate hoje) {
        List<TreinoLog> logs = treinoLogRepository.findByAlunoIdOrderByDataDesc(alunoId);
        int sequencia = 0;
        LocalDate esperado = hoje;

        for (TreinoLog log : logs) {
            if (log.getData().equals(esperado) && log.isConcluido()) {
                sequencia++;
                esperado = esperado.minusDays(1);
            } else if (log.getData().isBefore(esperado)) {
                break;
            }
        }
        return sequencia;
    }
}