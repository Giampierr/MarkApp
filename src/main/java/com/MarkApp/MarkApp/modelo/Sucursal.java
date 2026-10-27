
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public Integer getRadioGeocercaMetros() {
        return radioGeocercaMetros;
    }

    public void setRadioGeocercaMetros(Integer radioGeocercaMetros) {
        this.radioGeocercaMetros = radioGeocercaMetros;
    }

    public Integer getPrecisionGpsMaximaMetros() {
        return precisionGpsMaximaMetros;
    }

    public void setPrecisionGpsMaximaMetros(Integer precisionGpsMaximaMetros) {
        this.precisionGpsMaximaMetros = precisionGpsMaximaMetros;
    }
}
