package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.dto.ColaboradorListadoDTO;
import com.MarkApp.MarkApp.modelo.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ColaboradorRepositorio extends JpaRepository<Colaborador,Integer> {

    Optional<Colaborador> findByCorreo(String correo);

    @Query(
            """
        SELECT new com.MarkApp.MarkApp.dto.ColaboradorListadoDTO(
            c.id,c.dni,c.nombres,c.apellidos,c.correo,c.cargo,c.jornada,c.estadoColaborador,d.nombreDispositivo,d.activo
        ) FROM Colaborador c LEFT JOIN DispositivoEmpleado d
            ON d.colaborador.id = c.id
            AND d.activo = true 
            ORDER BY c.apellidos,c.nombres
"""
    )
    List<ColaboradorListadoDTO> listarParaAdministracion();

    @Query(
            """
        SELECT c FROM Colaborador c WHERE NOT EXISTS (
            SELECT a.idAsignacion
            FROM AsignacionTurno a
            where a.colaborador.id = c.id
                AND a.fechaFin IS NULL 
        )
        ORDER BY c.apellidos, c.nombres
"""
    )
    List<Colaborador> buscarColaboradoresSinTurno();
}
