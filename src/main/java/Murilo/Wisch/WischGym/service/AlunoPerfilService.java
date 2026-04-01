package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.Matricula;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilResponse;
import Murilo.Wisch.WischGym.dto.aluno.AlunoPerfilUpdateRequest;
import Murilo.Wisch.WischGym.dto.aluno.MatriculaResumoDTO;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.MatriculaRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlunoPerfilService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final Cloudinary cloudinary;

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


    public String updateFoto(MultipartFile file) {

        Aluno alunoLogado = getAlunoLogado();

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "wischgym/perfil",
                            "public_id", "aluno_" + alunoLogado.getId(),
                            "overwrite", true,
                            "transformation", new Transformation()
                                    .width(400)
                                    .height(400)
                                    .crop("fill")
                                    .gravity("face")
                    )
            );

            String url = result.get("secure_url").toString();

            alunoRepository.updateFotoPerfil(alunoLogado.getId(), url);

            return url;

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao fazer upload da foto.",
                    e
            );
        }
    }
}
