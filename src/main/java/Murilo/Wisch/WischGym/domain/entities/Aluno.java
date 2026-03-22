package Murilo.Wisch.WischGym.domain.entities;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.domain.enums.ObjetivoAluno;
import Murilo.Wisch.WischGym.domain.enums.StatusAlunos;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alunos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Aluno {

    @ManyToOne
    @JoinColumn(name = "plano_id")
    private Plano plano;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private User professor;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefone;

    private LocalDate dataNascimento;


    private Double peso;

    private Double altura;

    @Enumerated(EnumType.STRING)
    private ObjetivoAluno objetivo;

    @Enumerated(EnumType.STRING)
    private StatusAlunos status;

    private LocalDateTime dataCadastro;

    private boolean ativo = true;

    private boolean inadimplente = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist(){
        this.dataCadastro = LocalDateTime.now();
        if (this.status == null){
            this.status = StatusAlunos.ATIVO;
        }
    }

}
