
package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.TurnoDTO;
import com.MarkApp.MarkApp.enums.DiaSemana;
import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.modelo.Turno;
import com.MarkApp.MarkApp.modelo.TurnoDia;
import com.MarkApp.MarkApp.repositorio.AsignacionTurnoRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import com.MarkApp.MarkApp.repositorio.TurnoRepositorio;
import com.MarkApp.MarkApp.util.ObtenerIdUsuarioAutenticado;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TurnoServicio {

    private final TurnoRepositorio turnoRepositorio;
    private final AsignacionTurnoRepositorio asignacionTurnoRepositorio;
    private final SupervisorRepositorio supervisorRepositorio;
    private final AuditoriaServicio auditoriaServicio;

    public TurnoServicio(
            TurnoRepositorio turnoRepositorio,
            AsignacionTurnoRepositorio asignacionTurnoRepositorio,
            SupervisorRepositorio supervisorRepositorio,
            AuditoriaServicio auditoriaServicio) {

        this.turnoRepositorio = turnoRepositorio;
        this.asignacionTurnoRepositorio = asignacionTurnoRepositorio;
        this.supervisorRepositorio = supervisorRepositorio;
        this.auditoriaServicio = auditoriaServicio;
    }

    private Supervisor obtenerSupervisorAutenticado() {
        int idSupervisor =
                ObtenerIdUsuarioAutenticado.obtenerIdUsuarioAutenticado();

        return supervisorRepositorio.findById(idSupervisor)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El usuario autenticado no es un supervisor"
                ));
    }

    @Transactional
    public Turno crearTurno(TurnoDTO turnoDTO) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        validarTurno(turnoDTO);

        Turno turno = new Turno();
        turno.setNombre(turnoDTO.nombre());
        turno.setHoraIngreso(turnoDTO.horaIngreso());
        turno.setInicioRefrigerio(turnoDTO.inicioRefrigerio());
        turno.setFinRefrigerio(turnoDTO.finRefrigerio());
        turno.setHoraSalida(turnoDTO.horaSalida());
        turno.setMinTolerancia(turnoDTO.minTolerancia());

        for (DiaSemana dia : turnoDTO.diasSemana()) {
            TurnoDia turnoDia = new TurnoDia(turno, dia);
            turno.getDias().add(turnoDia);
        }

        Turno guardado = turnoRepositorio.save(turno);

        String detalle = "Se creó el turno '" + guardado.getNombre()
                + "' (ID: " + guardado.getIdTurno() + "). "
                + "Ingreso: " + guardado.getHoraIngreso()
                + ", salida: " + guardado.getHoraSalida()
                + ", refrigerio: " + guardado.getInicioRefrigerio()
                + " - " + guardado.getFinRefrigerio()
                + ", tolerancia: " + guardado.getMinTolerancia()
                + " minutos.";

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Turno,
                "Crear",
                detalle
        );

        return guardado;
    }

    @Transactional
    public Turno editarTurno(int idTurno, TurnoDTO turnoDTO) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        validarTurno(turnoDTO);

        Turno turno = turnoRepositorio.findById(idTurno)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Turno no encontrado"
                ));

        String nombreAnterior = turno.getNombre();

        turno.setNombre(turnoDTO.nombre());
        turno.setHoraIngreso(turnoDTO.horaIngreso());
        turno.setInicioRefrigerio(turnoDTO.inicioRefrigerio());
        turno.setFinRefrigerio(turnoDTO.finRefrigerio());
        turno.setHoraSalida(turnoDTO.horaSalida());
        turno.setMinTolerancia(turnoDTO.minTolerancia());

        turno.getDias().clear();

        for (DiaSemana dia : turnoDTO.diasSemana()) {
            turno.getDias().add(new TurnoDia(turno, dia));
        }

        Turno actualizado = turnoRepositorio.save(turno);

        String detalle = "Se editó el turno ID: " + idTurno
                + ", nombre anterior: '" + nombreAnterior
                + "', nombre actual: '" + actualizado.getNombre()
                + "'. Nuevo horario: ingreso "
                + actualizado.getHoraIngreso()
                + ", salida " + actualizado.getHoraSalida()
                + ", refrigerio " + actualizado.getInicioRefrigerio()
                + " - " + actualizado.getFinRefrigerio()
                + ", tolerancia " + actualizado.getMinTolerancia()
                + " minutos. Días: " + turnoDTO.diasSemana();

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Turno,
                "Editar",
                detalle
        );

        return actualizado;
    }

    @Transactional
    public void eliminarTurno(int idTurno) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        Turno turno = turnoRepositorio.findById(idTurno)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Turno no encontrado"
                ));

        if (asignacionTurnoRepositorio.existsByTurno_IdTurno(idTurno)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el turno porque tiene asignaciones"
            );
        }

        String nombreTurno = turno.getNombre();

        turnoRepositorio.delete(turno);

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Turno,
                "Eliminar",
                "Se eliminó el turno '" + nombreTurno
                        + "' (ID: " + idTurno + ")"
        );
    }

    private void validarTurno(TurnoDTO dto) {

        if (!dto.horaIngreso().isBefore(dto.horaSalida())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de ingreso debe ser anterior a la salida"
            );
        }

        if (!dto.inicioRefrigerio().isBefore(dto.finRefrigerio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El inicio del refrigerio debe ser anterior a su fin"
            );
        }

        if (dto.inicioRefrigerio().isBefore(dto.horaIngreso())
                || dto.finRefrigerio().isAfter(dto.horaSalida())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El refrigerio debe estar dentro del horario laboral"
            );
        }

        if (dto.diasSemana() == null || dto.diasSemana().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar al menos un día para el turno"
            );
        }

        if (dto.diasSemana().stream().distinct().count()
                != dto.diasSemana().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes repetir días en un turno"
            );
        }
    }
}
