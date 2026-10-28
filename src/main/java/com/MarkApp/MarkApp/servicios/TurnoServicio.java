package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.TurnoDTO;
import com.MarkApp.MarkApp.enums.DiaSemana;
import com.MarkApp.MarkApp.modelo.Turno;
import com.MarkApp.MarkApp.modelo.TurnoDia;
import com.MarkApp.MarkApp.repositorio.AsignacionTurnoRepositorio;
import com.MarkApp.MarkApp.repositorio.TurnoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TurnoServicio {
    private TurnoRepositorio turnoRepositorio;
    private AsignacionTurnoRepositorio asignacionTurnoRepositorio;

    public TurnoServicio(TurnoRepositorio turnoRepositorio, AsignacionTurnoRepositorio asignacionTurnoRepositorio) {
        this.turnoRepositorio = turnoRepositorio;
        this.asignacionTurnoRepositorio = asignacionTurnoRepositorio;
    }

    @Transactional
    public Turno crearTurno(TurnoDTO turnoDTO){

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

        return turnoRepositorio.save(turno);
    }

    @Transactional
    public Turno editarTurno(int idTurno, TurnoDTO turnoDTO){
        validarTurno(turnoDTO);

        Turno turno = turnoRepositorio.findById(idTurno).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Turno no encontrado"));

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

        return turnoRepositorio.save(turno);
    }

    @Transactional
    public void eliminarTurno(int idTurno){
        Turno turno = turnoRepositorio.findById(idTurno).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Turno no encontrado"));

        if (asignacionTurnoRepositorio.existsByTurno_IdTurno(idTurno)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"No se puede eliminar el turno porque tiene asignaciones");
        }

        turnoRepositorio.delete(turno);
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

        if (dto.diasSemana().stream().distinct().count()
                != dto.diasSemana().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes repetir días en un turno"
            );
        }
    }
}
