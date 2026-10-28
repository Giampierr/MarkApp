package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turno")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTurno;

    private String nombre;

    private LocalTime horaIngreso;
    private LocalTime inicioRefrigerio;
    private LocalTime finRefrigerio;
    private LocalTime horaSalida;
    private int minTolerancia;

    @OneToMany(
            mappedBy = "turno",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TurnoDia> dias = new ArrayList<>();

    public Turno() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public LocalTime getInicioRefrigerio() {
        return inicioRefrigerio;
    }

    public void setInicioRefrigerio(LocalTime inicioRefrigerio) {
        this.inicioRefrigerio = inicioRefrigerio;
    }

    public LocalTime getFinRefrigerio() {
        return finRefrigerio;
    }

    public void setFinRefrigerio(LocalTime finRefrigerio) {
        this.finRefrigerio = finRefrigerio;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getMinTolerancia() {
        return minTolerancia;
    }

    public void setMinTolerancia(int minTolerancia) {
        this.minTolerancia = minTolerancia;
    }

    public List<TurnoDia> getDias() {
        return dias;
    }

    public void setDias(List<TurnoDia> dias) {
        this.dias = dias;
    }
}
