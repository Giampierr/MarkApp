package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "boleta_pago")
public class BoletaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBoleta;

    @ManyToOne
    @JoinColumn(name = "id_colaborador",nullable = false)
    private Colaborador colaborador;

    private String periodo;
    private LocalDate fechaPago;
    private BigDecimal remBasica;
    private BigDecimal horExtras;
    private BigDecimal otrosIngresos;
    private BigDecimal afpAporteComisionSeguro;
    private BigDecimal otrosDescuentos;

    public BoletaPago() {
    }
}
