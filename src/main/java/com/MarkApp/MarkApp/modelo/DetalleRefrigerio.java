package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "detalle_refrigerio",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_refrigerio_colaborador_fecha",
                columnNames = {"id_colaborador","fecha"}
        )
        })
public class DetalleRefrigerio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRefrigerio;

    @ManyToOne
    @JoinColumn(name = "id_colaborador",nullable = false)
    private Colaborador colaborador;

    private LocalDate fecha;

    private LocalTime horaSalidaRefrigerio;

    @Column(nullable = true)
    private LocalTime horaRetornoRefrigerio;

    public DetalleRefrigerio() {
    }
}
