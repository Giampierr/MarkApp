package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_empleado")
public class DispositivoEmpleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDispositivo;

    @ManyToOne
    @JoinColumn(name = "id_colaborador",nullable = false)
    private Colaborador colaborador;

    @Column(nullable = false,unique = true)
    private String identificadorInstalacion;

    private String nombreDispositivo;
    private String plataforma;

    @Column(nullable = true)
    private LocalDateTime fechaVinculacion;

    @Column(nullable = false)
    private boolean activo = false;

    public DispositivoEmpleado() {
    }

    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public String getIdentificadorInstalacion() {
        return identificadorInstalacion;
    }

    public void setIdentificadorInstalacion(String identificadorInstalacion) {
        this.identificadorInstalacion = identificadorInstalacion;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public LocalDateTime getFechaVinculacion() {
        return fechaVinculacion;
    }

    public void setFechaVinculacion(LocalDateTime fechaVinculacion) {
        this.fechaVinculacion = fechaVinculacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
