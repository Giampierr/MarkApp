package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.Modulos;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuditoria;

    private LocalDateTime fechaYHora;

    @ManyToOne
    @JoinColumn(name = "id_supervisor",nullable = false)
    private Supervisor supervisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "modulo",nullable = false)
    private Modulos modulo;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private String detalle;

    public Auditoria() {
    }

    public LocalDateTime getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(LocalDateTime fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public Modulos getModulo() {
        return modulo;
    }

    public void setModulo(Modulos modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
