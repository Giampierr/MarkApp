package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.AsignarTurnoDTO;
import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.modelo.AsignacionTurno;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.modelo.Turno;
import com.MarkApp.MarkApp.repositorio.AsignacionTurnoRepositorio;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import com.MarkApp.MarkApp.repositorio.TurnoRepositorio;
import com.MarkApp.MarkApp.util.ObtenerIdUsuarioAutenticado;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsignarTurnoServicio {
    private ColaboradorRepositorio colaboradorRepositorio;
    private TurnoRepositorio turnoRepositorio;
    private AsignacionTurnoRepositorio asignacionTurnoRepositorio;
    private SupervisorRepositorio supervisorRepositorio;
    private AuditoriaServicio auditoriaServicio;

    public AsignarTurnoServicio(ColaboradorRepositorio colaboradorRepositorio, TurnoRepositorio turnoRepositorio, AsignacionTurnoRepositorio asignacionTurnoRepositorio, SupervisorRepositorio supervisorRepositorio, AuditoriaServicio auditoriaServicio) {
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.turnoRepositorio = turnoRepositorio;
        this.asignacionTurnoRepositorio = asignacionTurnoRepositorio;
        this.supervisorRepositorio = supervisorRepositorio;
        this.auditoriaServicio = auditoriaServicio;
    }


    @Transactional
    public void asignarTurno(AsignarTurnoDTO dto) {

        int idSupervisor =
                ObtenerIdUsuarioAutenticado.obtenerIdUsuarioAutenticado();

        Supervisor supervisor = supervisorRepositorio
                .findById(idSupervisor)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El usuario autenticado no es un supervisor"
                ));

        Turno turno = turnoRepositorio.findById(dto.idTurno())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Turno no encontrado"
                ));

        if (dto.fechaInicio().getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La asignación de turnos debe realizarse el domingo"
            );
        }

        LocalDate fechaInicio = dto.fechaInicio().plusDays(1);
        LocalDate fechaFin = fechaInicio.plusDays(6);

        List<String> nombres = new ArrayList<>();

        for (Integer idColaborador : dto.idColaboradores()) {

            Colaborador colaborador = colaboradorRepositorio
                    .findById(idColaborador)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Colaborador no encontrado: " + idColaborador
                    ));

            if (asignacionTurnoRepositorio
                    .existsByColaborador_IdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                            idColaborador, fechaFin, fechaInicio)) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El colaborador " + idColaborador
                                + " ya tiene una asignación en ese período"
                );
            }

            AsignacionTurno asignacion = new AsignacionTurno();
            asignacion.setColaborador(colaborador);
            asignacion.setTurno(turno);
            asignacion.setFechaInicio(fechaInicio);
            asignacion.setFechaFin(fechaFin);

            asignacionTurnoRepositorio.save(asignacion);

            nombres.add(
                    colaborador.getNombres() + " "
                            + colaborador.getApellidos()
            );
        }

        String detalle = "Se asignó el turno '" + turno.getNombre()
                + "' del " + fechaInicio + " al " + fechaFin
                + " a los colaboradores: "
                + String.join(", ", nombres);

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Turno,
                "Asignar",
                detalle
        );
    }

}
