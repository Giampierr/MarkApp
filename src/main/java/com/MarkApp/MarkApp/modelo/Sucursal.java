
package com.MarkApp.MarkApp.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSucursal;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(nullable = false)
    private Integer radioGeocercaMetros;

    @Column(nullable = false)
    private Integer precisionGpsMaximaMetros;

    public Sucursal() {
    }
}
