package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.DispositivoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispositivoEmpleadoRepositorio extends JpaRepository<DispositivoEmpleado,Integer> {


    boolean existsByColaborador_IdAndActivoTrue(Integer idDispositivo);

    Optional<DispositivoEmpleado>findByColaborador_IdAndActivoTrue(Integer idColaborador);
}
