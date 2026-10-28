package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.AsignacionTurno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionTurnoRepositorio extends JpaRepository<AsignacionTurno,Integer> {
    boolean existsByTurno_IdTurno(int idTurno);

    boolean existsByColaborador_IdAndFechaFinIsNull(int idColaborador);
}
