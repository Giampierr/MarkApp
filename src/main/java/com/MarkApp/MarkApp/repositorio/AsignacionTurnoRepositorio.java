package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.AsignacionTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AsignacionTurnoRepositorio extends JpaRepository<AsignacionTurno,Integer> {
    boolean existsByTurno_IdTurno(int idTurno);

    boolean existsByColaborador_IdAndFechaFinIsNull(int idColaborador);

    boolean existsByColaborador_IdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Integer idColaborador,
            LocalDate fechaFin,
            LocalDate fechaInicio
    );
}
