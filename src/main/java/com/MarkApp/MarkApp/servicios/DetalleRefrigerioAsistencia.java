
package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.enums.Jornada;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.DetalleAsistencia;
import com.MarkApp.MarkApp.modelo.DetalleRefrigerio;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.DetalleAsistenciaRepositorio;
import com.MarkApp.MarkApp.repositorio.DetalleRefrigerioRepositorio;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class DetalleRefrigerioAsistencia {

    private static final ZoneId ZONA_PERU =
            ZoneId.of("America/Lima");

    private static final LocalTime HORA_REFRIGERIO =
            LocalTime.of(13, 0);

    private static final LocalTime HORA_REFRIGERIO_REGRESO =
            LocalTime.of(14, 0);

    private static final int TOLERANCIA_REGRESO_MINUTOS = 10;

    private final DetalleRefrigerioRepositorio detalleRefrigerioRepositorio;
    private final ColaboradorRepositorio colaboradorRepositorio;
    private final DetalleAsistenciaRepositorio detalleAsistenciaRepositorio;

    public DetalleRefrigerioAsistencia(
            DetalleRefrigerioRepositorio detalleRefrigerioRepositorio,
            ColaboradorRepositorio colaboradorRepositorio,
            DetalleAsistenciaRepositorio detalleAsistenciaRepositorio) {

        this.detalleRefrigerioRepositorio = detalleRefrigerioRepositorio;
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.detalleAsistenciaRepositorio = detalleAsistenciaRepositorio;
    }

    @Transactional
    public DetalleRefrigerio registrarSalidaRefrigerio(
            int idColaborador) {

        LocalDate fecha = LocalDate.now(ZONA_PERU);
        LocalTime horaActual = LocalTime.now(ZONA_PERU);

        Colaborador colaborador =
                colaboradorRepositorio.findById(idColaborador)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador no encontrado"));

        validarJornadaFull(colaborador);

        DetalleAsistencia asistencia =
                detalleAsistenciaRepositorio
                        .findByColaborador_IdAndFecha(idColaborador, fecha).orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "No registró asistencia el día de hoy"));

        if (asistencia.getHoraIngreso() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Debe registrar su ingreso antes del refrigerio"
            );
        }

        if (horaActual.isBefore(HORA_REFRIGERIO)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El refrigerio se habilita a las 13:00"
            );
        }

        if (detalleRefrigerioRepositorio
                .findByColaborador_IdAndFecha(
                        idColaborador, fecha)
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya registró su refrigerio el día de hoy"
            );
        }

        DetalleRefrigerio refrigerio = new DetalleRefrigerio();
        refrigerio.setColaborador(colaborador);
        refrigerio.setFecha(fecha);
        refrigerio.setHoraSalidaRefrigerio(horaActual);

        return detalleRefrigerioRepositorio.save(refrigerio);
    }

    @Transactional
    public DetalleRefrigerio registrarRegresoRefrigerio(
            int idColaborador) {

        LocalDate fecha = LocalDate.now(ZONA_PERU);
        LocalTime horaActual = LocalTime.now(ZONA_PERU);

        Colaborador colaborador =
                colaboradorRepositorio.findById(idColaborador)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador no encontrado"
                                )
                        );

        validarJornadaFull(colaborador);

        DetalleAsistencia asistencia =
                detalleAsistenciaRepositorio
                        .findByColaborador_IdAndFecha(
                                idColaborador, fecha)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "No registró asistencia el día de hoy"
                                )
                        );

        if (asistencia.getHoraIngreso() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Debe registrar su ingreso antes del refrigerio"
            );
        }

        DetalleRefrigerio refrigerio =
                detalleRefrigerioRepositorio
                        .findByColaborador_IdAndFecha(
                                idColaborador, fecha)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "No registró la salida al refrigerio hoy"
                                )
                        );

        if (refrigerio.getHoraSalidaRefrigerio() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Debe registrar la salida al refrigerio primero"
            );
        }

        if (refrigerio.getHoraRetornoRefrigerio() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya registró el regreso del refrigerio"
            );
        }

        if (horaActual.isBefore(HORA_REFRIGERIO_REGRESO)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El regreso del refrigerio se habilita a las 14:00"
            );
        }

        long minutosTardanza = Math.max(
                0,
                Duration.between(
                        HORA_REFRIGERIO_REGRESO,
                        horaActual
                ).toMinutes()
        );

        boolean regresoTardio =
                horaActual.isAfter(
                        HORA_REFRIGERIO_REGRESO
                                .plusMinutes(TOLERANCIA_REGRESO_MINUTOS)
                );

        refrigerio.setHoraRetornoRefrigerio(horaActual);

        return detalleRefrigerioRepositorio.save(refrigerio);
    }

    private void validarJornadaFull(Colaborador colaborador) {
        if (colaborador.getJornada() != Jornada.Full) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La jornada del colaborador no incluye refrigerio"
            );
        }
    }
}
