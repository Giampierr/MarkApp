package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "detalle_asistencia")
public class DetalleAsistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAsistencia;

    @ManyToOne
    @JoinColumn(name = "id_colaborador",nullable = false)
    private Colaborador colaborador;

    private LocalTime horaIngreso;
    private String firmaIngreso;
    private LocalTime horaSalida;
    private String firmaSalida;

    public DetalleAsistencia() {
    }


}
