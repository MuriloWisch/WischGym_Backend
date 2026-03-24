package Murilo.Wisch.WischGym.domain.entities;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.enums.DivisaoTreino;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "treinos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String diaTreino;

    @Enumerated(EnumType.STRING)
    private DivisaoTreino divisao;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private User professor;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreinoExercicio> exercicios;

    private LocalDateTime dataCriacao;

    private boolean ativo = true;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}