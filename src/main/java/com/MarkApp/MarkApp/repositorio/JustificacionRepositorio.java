package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.dto.JustificacionListadoDTO;
import com.MarkApp.MarkApp.modelo.Justificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JustificacionRepositorio extends JpaRepository<Justificacion,Integer> {

    @Query("""
    SELECT new com.MarkApp.MarkApp.dto.JustificacionListadoDTO(
        j.idJustificacion,
        c.nombres,
        c.apellidos,
        j.tipoAsistencia,
        CAST(j.horaMensajeColaborador AS LocalDate),
        j.mensajeColaborador,
        j.archivoColaborador,
        j.estadoJustificacion
    )
    FROM Justificacion j
    JOIN j.colaborador c
    ORDER BY j.horaMensajeColaborador DESC
""")
    List<JustificacionListadoDTO> listarParaAdministracion();

}
