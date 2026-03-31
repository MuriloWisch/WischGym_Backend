package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.entities.Aluno;
import Murilo.Wisch.WischGym.domain.entities.Convite;
import Murilo.Wisch.WischGym.domain.enums.Roles;
import Murilo.Wisch.WischGym.domain.enums.StatusConvite;
import Murilo.Wisch.WischGym.domain.enums.TipoNotificacao;
import Murilo.Wisch.WischGym.dto.convite.ConviteEnviarDTO;
import Murilo.Wisch.WischGym.dto.convite.ConviteResponseDTO;
import Murilo.Wisch.WischGym.repository.AlunoRepository;
import Murilo.Wisch.WischGym.repository.ConviteRepository;
import Murilo.Wisch.WischGym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConviteService {

    private final ConviteRepository conviteRepository;
    private final UserRepository userRepository;
    private final AlunoRepository alunoRepository;
    private final NotificacaoService notificacaoService;


    public ConviteResponseDTO enviar(String emailProfessor, ConviteEnviarDTO dto) {

        User professor = userRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        if (!professor.getRoles().contains(Roles.PROFESSOR)) {
            throw new RuntimeException("Usuário não é professor");
        }

        Aluno aluno = alunoRepository.findByEmail(dto.getEmailAluno())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com esse email"));

        if (aluno.getProfessor() != null) {
            throw new RuntimeException("Este aluno já possui um professor vinculado");
        }

        boolean jaExiste = conviteRepository.existsByProfessorIdAndAlunoIdAndStatus(
                professor.getId(), aluno.getId(), StatusConvite.PENDENTE
        );

        if (jaExiste) {
            throw new RuntimeException("Já existe um convite pendente para este aluno");
        }

        Convite convite = new Convite();
        convite.setProfessor(professor);
        convite.setAluno(aluno);
        convite.setStatus(StatusConvite.PENDENTE);
        convite.setDataCriacao(LocalDateTime.now());

        Convite salvo = (conviteRepository.save(convite));

        notificacaoService.criar(
                aluno.getUser(),
                TipoNotificacao.CONVITE_RECEBIDO,
                "Convite recebido",
                "O professor " + professor.getNome() + " te convidou para treinar juntos."
        );
        return toDTO(salvo);
    }

    public List<ConviteResponseDTO> listarRecebidos(String emailAluno) {
        Aluno aluno = alunoRepository.findByEmail(emailAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return conviteRepository.findByAlunoIdOrderByDataCriacaoDesc(aluno.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ConviteResponseDTO> listarEnviados(String emailProfessor) {
        User professor = userRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        return conviteRepository.findByProfessorIdOrderByDataCriacaoDesc(professor.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ConviteResponseDTO aceitar(Long conviteId, String emailAluno) {
        Convite convite = buscarConviteValidado(conviteId, emailAluno);

        convite.setStatus(StatusConvite.ACEITO);
        convite.setDataResposta(LocalDateTime.now());
        conviteRepository.save(convite);

        Aluno aluno = convite.getAluno();
        aluno.setProfessor(convite.getProfessor());
        alunoRepository.save(aluno);

        conviteRepository.findByAlunoIdOrderByDataCriacaoDesc(aluno.getId())
                .stream()
                .filter(c -> c.getStatus() == StatusConvite.PENDENTE && !c.getId().equals(conviteId))
                .forEach(c -> {
                    c.setStatus(StatusConvite.RECUSADO);
                    c.setDataResposta(LocalDateTime.now());
                    conviteRepository.save(c);
                });

        return toDTO(convite);
    }

    public ConviteResponseDTO recusar(Long conviteId, String emailAluno) {
        Convite convite = buscarConviteValidado(conviteId, emailAluno);

        convite.setStatus(StatusConvite.RECUSADO);
        convite.setDataResposta(LocalDateTime.now());

        return toDTO(conviteRepository.save(convite));
    }

    private Convite buscarConviteValidado(Long conviteId, String emailAluno) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        if (!convite.getAluno().getEmail().equals(emailAluno)) {
            throw new RuntimeException("Este convite não pertence a você");
        }

        if (convite.getStatus() != StatusConvite.PENDENTE) {
            throw new RuntimeException("Este convite já foi respondido");
        }

        return convite;
    }

    private ConviteResponseDTO toDTO(Convite convite) {
        return new ConviteResponseDTO(
                convite.getId(),
                convite.getProfessor().getNome(),
                convite.getProfessor().getEmail(),
                convite.getAluno().getNome(),
                convite.getAluno().getEmail(),
                convite.getStatus(),
                convite.getDataCriacao(),
                convite.getDataResposta()
        );
    }


}
