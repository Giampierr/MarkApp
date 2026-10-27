package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.TipoAsistencia;
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

    private LocalTime horaSalida;


    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asistencia",nullable = false)
    private TipoAsistencia tipoAsistencia;

    public DetalleAsistencia() {
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

    public LocalTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }


    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }


    public TipoAsistencia getTipoAsistencia() {
        return tipoAsistencia;
    }

    public void setTipoAsistencia(TipoAsistencia tipoAsistencia) {
        this.tipoAsistencia = tipoAsistencia;
    }
}
