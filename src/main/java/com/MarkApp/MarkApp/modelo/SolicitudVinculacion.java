
package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.EstadoVinculacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_vinculacion")
public class SolicitudVinculacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", nullable = false)
    private Colaborador colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dispositivo_solicitado", nullable = false)
    private DispositivoEmpleado dispositivoSolicitado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dispositivo_anterior")
    private DispositivoEmpleado dispositivoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVinculacion estado = EstadoVinculacion.Pendiente;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supervisor")
    private Supervisor supervisor;

    private LocalDateTime fechaRevision;

    @Column(length = 255)
    private String motivoRechazo;

    public SolicitudVinculacion() {
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public DispositivoEmpleado getDispositivoSolicitado() {
        return dispositivoSolicitado;
    }

    public void setDispositivoSolicitado(DispositivoEmpleado dispositivoSolicitado) {
        this.dispositivoSolicitado = dispositivoSolicitado;
    }

    public DispositivoEmpleado getDispositivoAnterior() {
        return dispositivoAnterior;
    }

    public void setDispositivoAnterior(DispositivoEmpleado dispositivoAnterior) {
        this.dispositivoAnterior = dispositivoAnterior;
    }

    public EstadoVinculacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVinculacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    @PrePersist
    protected void alCrear() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoVinculacion.Pendiente;
        }
    }

}
