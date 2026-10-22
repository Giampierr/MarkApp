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

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol",nullable = false)
    private RolSupervisor rolSupervisor;

    public Supervisor() {
    }


}
