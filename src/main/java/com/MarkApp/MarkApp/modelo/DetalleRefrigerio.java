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

    @Column(name = "hora_retorno_refrigerio", nullable = true)
    private LocalTime horaRetornoRefrigerio;

    public DetalleRefrigerio() {
    }

    public Long getIdRefrigerio() {
        return idRefrigerio;
    }

    public void setIdRefrigerio(Long idRefrigerio) {
        this.idRefrigerio = idRefrigerio;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraRetornoRefrigerio() {
        return horaRetornoRefrigerio;
    }

    public void setHoraRetornoRefrigerio(LocalTime horaRetornoRefrigerio) {
        this.horaRetornoRefrigerio = horaRetornoRefrigerio;
    }

    public LocalTime getHoraSalidaRefrigerio() {
        return horaSalidaRefrigerio;
    }

    public void setHoraSalidaRefrigerio(LocalTime horaSalidaRefrigerio) {
        this.horaSalidaRefrigerio = horaSalidaRefrigerio;
    }
}
