package Murilo.Wisch.WischGym.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicio_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExercicioLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "treino_log_id", nullable = false)
    private TreinoLog treinoLog;

    @ManyToOne
    @JoinColumn(name = "treino_exercicio_id", nullable = false)
    private TreinoExercicio treinoExercicio;

    private boolean concluido = false;
}