package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.AsignarTurnoDTO;
import com.MarkApp.MarkApp.modelo.AsignacionTurno;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.Turno;
import com.MarkApp.MarkApp.repositorio.AsignacionTurnoRepositorio;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.TurnoRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AsignarTurnoServicio {
    private ColaboradorRepositorio colaboradorRepositorio;
    private TurnoRepositorio turnoRepositorio;
    private AsignacionTurnoRepositorio asignacionTurnoRepositorio;

    public AsignarTurnoServicio(ColaboradorRepositorio colaboradorRepositorio, TurnoRepositorio turnoRepositorio, AsignacionTurnoRepositorio asignacionTurnoRepositorio) {
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.turnoRepositorio = turnoRepositorio;
        this.asignacionTurnoRepositorio = asignacionTurnoRepositorio;
    }

    public void asignarTurno(AsignarTurnoDTO asignarTurnoDTO){

        Turno turno = turnoRepositorio.findById(asignarTurnoDTO.idTurno()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Turno no encontrado"));

        for (Integer idColaborador : asignarTurnoDTO.idColaboradores()){
            Colaborador colaborador = colaboradorRepositorio.findById(idColaborador).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,"Colaborador no encontrado :"+idColaborador));

            if (asignacionTurnoRepositorio.existsByColaborador_IdAndFechaFinIsNull(idColaborador)){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"El colaborador :"+idColaborador+ " ya tiene una asignacion de turno vigente");
            }

            AsignacionTurno asignacion = new AsignacionTurno();
            asignacion.setColaborador(colaborador);
            asignacion.setTurno(turno);
            asignacion.setFechaFin(asignarTurnoDTO.fechaInicio());

            asignacionTurnoRepositorio.save(asignacion);
        }
    }
}
