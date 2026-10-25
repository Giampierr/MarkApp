package com.MarkApp.MarkApp.modelo;

import com.MarkApp.MarkApp.enums.Cargos;
import com.MarkApp.MarkApp.enums.RolSupervisor;
import jakarta.persistence.*;

@Entity
@Table(name = "supervisor")
public class Supervisor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSupervisor;

    private String nombre;

    @Column(nullable = false,unique = true)
    private String correo;

    @Column(nullable = false,length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol",nullable = false)
    private RolSupervisor rolSupervisor;

    public Supervisor() {
    }

    public int getIdSupervisor() {
        return idSupervisor;
    }

    public void setIdSupervisor(int idSupervisor) {
        this.idSupervisor = idSupervisor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public RolSupervisor getRolSupervisor() {
        return rolSupervisor;
    }

    public void setRolSupervisor(RolSupervisor rolSupervisor) {
        this.rolSupervisor = rolSupervisor;
    }
}
