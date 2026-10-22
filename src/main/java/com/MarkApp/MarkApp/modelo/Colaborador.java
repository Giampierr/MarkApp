package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.Cargos;
import com.MarkApp.MarkApp.enums.Jornada;
import com.MarkApp.MarkApp.enums.RegimenPensionario;
import jakarta.persistence.*;


@Entity
@Table(name = "colaborador")
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombres;
    private String apellidos;
    private String dni;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo")
    private Cargos cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "jornada")
    private Jornada jornada;

    @Enumerated(EnumType.STRING)
    @Column(name = "regimen_pensionario")
    private RegimenPensionario regimenPensionario;

    @Column(nullable = false,unique = true)
    private String correo;
    private String password;
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
        this.dni = dni;
    }

    public Cargos getCargo() {
        return cargo;
    }

    public void setCargo(Cargos cargo) {
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
}
