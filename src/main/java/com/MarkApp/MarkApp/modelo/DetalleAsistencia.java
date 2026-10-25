package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "detalle_asistencia",
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_asistencia_colaborador_fecha",
                columnNames = {"id_colaborador","fecha"}
        )
})
public class DetalleAsistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAsistencia;

    @ManyToOne
    @JoinColumn(name = "id_colaborador",nullable = false)
    private Colaborador colaborador;

    @Column(nullable = false)
    private LocalDate fecha;

    private LocalTime horaIngreso;
    private String firmaIngreso;
    private LocalTime horaSalida;
    private String firmaSalida;

    public DetalleAsistencia() {
    }


}
