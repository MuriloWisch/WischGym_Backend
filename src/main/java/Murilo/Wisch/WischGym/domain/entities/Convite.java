package Murilo.Wisch.WischGym.domain.entities;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.enums.StatusConvite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "convites")
@Getter
@Setter
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConvite status = StatusConvite.PENDENTE;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    private LocalDateTime dataResposta;
}
