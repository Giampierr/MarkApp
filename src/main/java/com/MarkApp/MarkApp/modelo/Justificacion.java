package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.EstadoJustificacion;
import com.MarkApp.MarkApp.enums.TipoAsistencia;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    @Column(name = "archivo_colaborador" ,length = 1000)
    private String archivoColaborador;

    private LocalDateTime horaMensajeColaborador;
    private String respuestaSupervisor;
    private LocalDateTime horaRespuestaSupervisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_justificacion",nullable = false)
    private TipoAsistencia tipoAsistencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado",nullable = false)
    private EstadoJustificacion estadoJustificacion;

    public Justificacion() {
    }

    public int getIdJustificacion() {
        return idJustificacion;
    }

    public void setIdJustificacion(int idJustificacion) {
        this.idJustificacion = idJustificacion;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public String getMensajeColaborador() {
        return mensajeColaborador;
    }

    public void setMensajeColaborador(String mensajeColaborador) {
        this.mensajeColaborador = mensajeColaborador;
    }

    public String getArchivoColaborador() {
        return archivoColaborador;
    }

    public void setArchivoColaborador(String archivoColaborador) {
        this.archivoColaborador = archivoColaborador;
    }

    public LocalDateTime getHoraMensajeColaborador() {
        return horaMensajeColaborador;
    }

    public void setHoraMensajeColaborador(LocalDateTime horaMensajeColaborador) {
        this.horaMensajeColaborador = horaMensajeColaborador;
    }

    public String getRespuestaSupervisor() {
        return respuestaSupervisor;
    }

    public void setRespuestaSupervisor(String respuestaSupervisor) {
        this.respuestaSupervisor = respuestaSupervisor;
    }

    public LocalDateTime getHoraRespuestaSupervisor() {
        return horaRespuestaSupervisor;
    }

    public void setHoraRespuestaSupervisor(LocalDateTime horaRespuestaSupervisor) {
        this.horaRespuestaSupervisor = horaRespuestaSupervisor;
    }

    public EstadoJustificacion getEstadoJustificacion() {
        return estadoJustificacion;
    }

    public void setEstadoJustificacion(EstadoJustificacion estadoJustificacion) {
        this.estadoJustificacion = estadoJustificacion;
    }

    public TipoAsistencia getTipoAsistencia() {
        return tipoAsistencia;
    }

    public void setTipoAsistencia(TipoAsistencia tipoAsistencia) {
        this.tipoAsistencia = tipoAsistencia;
    }
}
