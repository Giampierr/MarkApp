package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.EstadoJustificacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

//Entidad: Justificación
@Entity
@Table(name = "justificacion")
public class Justificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idJustificacion;

    @ManyToOne
    @JoinColumn(name = "id_colaborador",nullable = false)
    private Colaborador colaborador;

    @ManyToOne
    @JoinColumn(name = "id_supervisor",nullable = true)
    private Supervisor supervisor;

    private String mensajeColaborador;
    private String archivoColaborador;
    private LocalDateTime horaMensajeColaborador;
    private String respuestaSupervisor;
    private LocalDateTime horaRespuestaSupervisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado",nullable = false)
    private EstadoJustificacion estadoJustificacion;

    public Justificacion() {
    }
}
