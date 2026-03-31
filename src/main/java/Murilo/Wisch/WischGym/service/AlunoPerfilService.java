package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.dto.aluno.AlunoFotoRequest;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilResponse;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilUpdateRequest;
import Murilo.Wisch.WischGym.dto.aluno.MatriculaResumoDTO;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class AlunoPerfilService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    private Aluno getAlunoLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return alunoRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }

    public AlunoPerfilResponse getPerfil() {
        Aluno aluno = getAlunoLogado();

        MatriculaResumoDTO matriculaDTO = matriculaRepository
                .findByAlunoId(aluno.getId())
                .stream()
                .max(Comparator.comparing(Matricula::getDataInicio))
                .map(m -> new MatriculaResumoDTO(
                        m.getId(),
                        m.getPlano().getNome(),
                        m.getPlano().getValor(),
                        m.getDataInicio(),
                        m.getDataFim(),
                        m.getStatus()
                ))
                .orElse(null);

        return new AlunoPerfilResponse(
                aluno.getId(),
                aluno.getUser().getNome(),
                aluno.getUser().getEmail(),
                aluno.getFotoPerfil(),
                aluno.getPeso(),
                aluno.getAltura(),
                aluno.getObjetivo(),
                aluno.getStatus(),
                matriculaDTO
        );
    }

    @Transactional
    public AlunoPerfilResponse updatePerfil(AlunoPerfilUpdateRequest request) {
        Aluno aluno = getAlunoLogado();

        if (request.peso() != null)     aluno.setPeso(request.peso());
        if (request.altura() != null)   aluno.setAltura(request.altura());
        if (request.objetivo() != null) aluno.setObjetivo(request.objetivo());

        alunoRepository.save(aluno);
        return getPerfil();
    }

    public void updateFoto(AlunoFotoRequest request) {
        Aluno aluno = getAlunoLogado();

        if (request.fotoPerfil().length() > 3_500_000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto muito grande. Máximo 2MB.");
        }

        aluno.setFotoPerfil(request.fotoPerfil());
        alunoRepository.save(aluno);
    }
}
