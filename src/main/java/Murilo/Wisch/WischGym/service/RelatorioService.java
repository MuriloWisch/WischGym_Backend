package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.dto.RelatorioMensalDTO;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
public class RelatorioService {

    private final AlunoRepository alunoRepository;

    public RelatorioService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public RelatorioMensalDTO gerarRelatorioMensal(Integer year, Integer month) {

        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException("O mês deve estar entre 1 e 12");
        }
        if (year != null && (year < 1900 || year > 2100)) {
            throw new IllegalArgumentException("O ano deve estar entre 1900 e 2100");
        }

        YearMonth ym;
        if (year == null || month == null) {
            ym = YearMonth.now(ZoneId.systemDefault());
        } else {
            ym = YearMonth.of(year, month);
        }

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        long totalAlunos = alunoRepository.count();
        long totalAtivos = alunoRepository.countByAtivoTrue();
        long totalInadimplentes = alunoRepository.countByInadimplenteTrue();
        long novosAlunosNoMes = alunoRepository.countByDataCadastroBetween(start, end);

        return new RelatorioMensalDTO(ym.getYear(), ym.getMonthValue(), totalAlunos, totalAtivos, totalInadimplentes, novosAlunosNoMes);
    }
}
