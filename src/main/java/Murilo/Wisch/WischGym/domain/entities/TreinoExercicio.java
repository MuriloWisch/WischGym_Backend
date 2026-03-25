package Murilo.Wisch.WischGym.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treino_exercicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreinoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @ManyToOne
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    private Integer series;

    private Integer repeticoes;

    private Integer descanso;

    private String observacao;

    private Integer ordem;
}