package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.EstadoColaborador;
import com.MarkApp.MarkApp.enums.Jornada;
import com.MarkApp.MarkApp.enums.RegimenPensionario;
import com.MarkApp.MarkApp.util.Validador;
import jakarta.persistence.*;

import java.util.Objects;


@Entity
@Table(name = "colaborador")
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombres;
    private String apellidos;

    @Column(unique = true,nullable = false,length = 8)
    private String dni;

    @Column(name = "cargo", nullable = false)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "jornada")
    private Jornada jornada;

    @Enumerated(EnumType.STRING)
    @Column(name = "regimen_pensionario")
    private RegimenPensionario regimenPensionario;

    @Column(nullable = false,unique = true)
    private String correo;

    @Column(nullable = false,length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoColaborador estadoColaborador;

    public Colaborador() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        Objects.requireNonNull(dni,"Dni no puede ser null");

        dni = dni.trim();

        if (Validador.validarDni(dni)) {
            this.dni = dni;
        } else {
            throw new IllegalArgumentException("Dni inválido");
        }
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public RegimenPensionario getRegimenPensionario() {
        return regimenPensionario;
    }

    public void setRegimenPensionario(RegimenPensionario regimenPensionario) {
        this.regimenPensionario = regimenPensionario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EstadoColaborador getEstadoColaborador() {
        return estadoColaborador;
    }

    public void setEstadoColaborador(EstadoColaborador estadoColaborador) {
        this.estadoColaborador = estadoColaborador;
    }
}
