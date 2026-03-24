package Murilo.Wisch.WischGym.domain.entities;

import Murilo.Wisch.WischGym.domain.enums.GrupoMuscular;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GrupoMuscular grupoMuscular;

    private String gifUrl;

    private String descricao;
}
