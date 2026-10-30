package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.DetalleAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DetalleAsistenciaRepositorio extends JpaRepository<DetalleAsistencia,Integer> {

    List<DetalleAsistencia> findByFecha(LocalDate fecha);

    List<DetalleAsistencia> findByColaborador_Id(int idColaborador);

    List<DetalleAsistencia> findByColaborador_IdAndFechaBetween(int idColaborador,LocalDate inicio,LocalDate fin);

    boolean existsByColaborador_IdAndFecha(int idColaborador, LocalDate fecha);

    Optional<DetalleAsistencia> findByColaborador_IdAndFecha(int idColaborador, LocalDate fecha);
}
