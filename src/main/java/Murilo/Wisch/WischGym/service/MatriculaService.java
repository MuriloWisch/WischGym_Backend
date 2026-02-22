package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.domain.enums.StatusMatricula;
import Murilo.Wisch.WischGym.dto.matricula.MatriculaCreateDTO;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import Murilo.Wisch.WischGym.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository, AlunoRepository alunoRepository, PlanoRepository planoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
    }

    public Matricula matricular(MatriculaCreateDTO dto){

        Aluno aluno = alunoRepository.findById(dto.getAlunoId()).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        Plano plano = planoRepository.findById(dto.getPlanoId()).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        Optional<Matricula> matriculaAtiva = matriculaRepository.findByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA);

        if (matriculaAtiva.isPresent()) {
            throw new RuntimeException("Aluno ja possui esta matricula ativa");
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

        return matriculaRepository.save(matricula);
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
        }
    }

    public Matricula renovar(Long id){
        Matricula matricula = matriculaRepository.findById(id).orElseThrow(() -> new RuntimeException("Matricula não encontrada"));

        Plano plano = matricula.getPlano();

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
        return matriculaRepository.save(matricula);
    }


}
