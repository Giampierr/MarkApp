package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.DetalleRefrigerio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DetalleRefrigerioRepositorio extends JpaRepository<DetalleRefrigerio,Long> {

    boolean existsByColaborador_IdAndFecha(int idColaborador, LocalDate fecha);
    Optional<DetalleRefrigerio> findByColaborador_IdAndFecha(int idColaborador, LocalDate fecha);
}
