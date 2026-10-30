package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.DiaSemana;
import jakarta.persistence.*;

@Entity
@Table(
        name = "turno_dia",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_turno", "dia"}
        )
)
public class TurnoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTurnoDia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_turno", nullable = false)
    private Turno turno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DiaSemana dia;

    public TurnoDia() {
    }

    public TurnoDia(Turno turno, DiaSemana dia) {
        this.turno = turno;
        this.dia = dia;
    }

    public Integer getIdTurnoDia() {
        return idTurnoDia;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }
}