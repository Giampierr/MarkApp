package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.enums.EstadoVinculacion;
import com.MarkApp.MarkApp.modelo.SolicitudVinculacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudVinculacionRepositorio extends JpaRepository<SolicitudVinculacion,Long> {

    List<SolicitudVinculacion> findByEstado(EstadoVinculacion estadoVinculacion);


}
