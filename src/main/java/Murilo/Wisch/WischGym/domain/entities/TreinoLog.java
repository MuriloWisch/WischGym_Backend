package Murilo.Wisch.WischGym.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "treino_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreinoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    private LocalDate data;

    private Integer porcentagemConcluida = 0;

    private boolean concluido = false;

    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "treinoLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExercicioLog> exerciciosLog;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.data == null) this.data = LocalDate.now();
    }
}