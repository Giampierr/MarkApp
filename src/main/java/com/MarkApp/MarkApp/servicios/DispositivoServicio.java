
package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.enums.EstadoVinculacion;
import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.DispositivoEmpleado;
import com.MarkApp.MarkApp.modelo.SolicitudVinculacion;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.DispositivoEmpleadoRepositorio;
import com.MarkApp.MarkApp.repositorio.SolicitudVinculacionRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import com.MarkApp.MarkApp.util.ObtenerIdUsuarioAutenticado;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class DispositivoServicio {

    private final DispositivoEmpleadoRepositorio dispositivoEmpleadoRepositorio;
    private final SolicitudVinculacionRepositorio solicitudVinculacionRepositorio;
    private final SupervisorRepositorio supervisorRepositorio;
    private final AuditoriaServicio auditoriaServicio;

    public DispositivoServicio(
            DispositivoEmpleadoRepositorio dispositivoEmpleadoRepositorio,
            SolicitudVinculacionRepositorio solicitudVinculacionRepositorio,
            SupervisorRepositorio supervisorRepositorio,
            AuditoriaServicio auditoriaServicio) {

        this.dispositivoEmpleadoRepositorio = dispositivoEmpleadoRepositorio;
        this.solicitudVinculacionRepositorio = solicitudVinculacionRepositorio;
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

    // Vincular el primer dispositivo de un colaborador
    @Transactional
    public DispositivoEmpleado vincularPrimerDispositivo(
            Colaborador colaborador,
            DispositivoEmpleado dispositivoEmpleado) {

        boolean tieneDispositivoActivo =
                dispositivoEmpleadoRepositorio
                        .existsByColaborador_IdAndActivoTrue(colaborador.getId());

        if (tieneDispositivoActivo) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El colaborador ya tiene un dispositivo activo"
            );
        }

        dispositivoEmpleado.setColaborador(colaborador);
        dispositivoEmpleado.setFechaVinculacion(LocalDateTime.now());
        dispositivoEmpleado.setActivo(true);

        return dispositivoEmpleadoRepositorio.save(dispositivoEmpleado);
    }

    // Solicitar un cambio de dispositivo
    @Transactional
    public SolicitudVinculacion solicitarCambioDispositivo(
            Colaborador colaborador,
            DispositivoEmpleado nuevoDispositivo) {

        int idColaborador = colaborador.getId();

        DispositivoEmpleado anterior =
                dispositivoEmpleadoRepositorio
                        .findByColaborador_IdAndActivoTrue(idColaborador)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No existe un dispositivo activo para cambiar"
                        ));

        nuevoDispositivo.setColaborador(colaborador);
        nuevoDispositivo.setActivo(false);
        nuevoDispositivo.setFechaVinculacion(null);

        DispositivoEmpleado guardado =
                dispositivoEmpleadoRepositorio.save(nuevoDispositivo);

        SolicitudVinculacion solicitud = new SolicitudVinculacion();
        solicitud.setColaborador(colaborador);
        solicitud.setDispositivoAnterior(anterior);
        solicitud.setDispositivoSolicitado(guardado);
        solicitud.setEstado(EstadoVinculacion.Pendiente);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        // Importante: guardar la solicitud
        return solicitudVinculacionRepositorio.save(solicitud);
    }

    // Aprobar el cambio de dispositivo
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SolicitudVinculacion aprobarSolicitud(Long idSolicitud) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        SolicitudVinculacion solicitud =
                solicitudVinculacionRepositorio.findById(idSolicitud)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La solicitud no existe"
                        ));

        if (solicitud.getEstado() != EstadoVinculacion.Pendiente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue procesada"
            );
        }

        DispositivoEmpleado anterior = solicitud.getDispositivoAnterior();
        DispositivoEmpleado nuevo = solicitud.getDispositivoSolicitado();

        if (nuevo == null || nuevo.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nuevo dispositivo no es válido"
            );
        }

        if (anterior != null) {
            anterior.setActivo(false);
            dispositivoEmpleadoRepositorio.save(anterior);
        }

        nuevo.setActivo(true);
        nuevo.setFechaVinculacion(LocalDateTime.now());
        dispositivoEmpleadoRepositorio.save(nuevo);

        solicitud.setSupervisor(supervisor);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setEstado(EstadoVinculacion.Aprobada);

        SolicitudVinculacion aprobada =
                solicitudVinculacionRepositorio.save(solicitud);

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Dispositivos,
                "Aprobar cambio",
                "Se aprobó la solicitud " + idSolicitud
                        + " del colaborador "
                        + solicitud.getColaborador().getNombres()
                        + " " + solicitud.getColaborador().getApellidos()
                        + ". Dispositivo nuevo ID: " + nuevo.getIdDispositivo()
        );

        return aprobada;
    }

    // Rechazar el cambio de dispositivo
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SolicitudVinculacion rechazarSolicitud(
            Long idSolicitud,
            String motivo) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        SolicitudVinculacion solicitud =
                solicitudVinculacionRepositorio.findById(idSolicitud)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La solicitud no existe"
                        ));

        if (solicitud.getEstado() != EstadoVinculacion.Pendiente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue procesada"
            );
        }

        DispositivoEmpleado nuevo = solicitud.getDispositivoSolicitado();

        if (nuevo != null) {
            nuevo.setActivo(false);
            dispositivoEmpleadoRepositorio.save(nuevo);
        }

        solicitud.setSupervisor(supervisor);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setEstado(EstadoVinculacion.Rechazada);
        solicitud.setMotivoRechazo(motivo);

        SolicitudVinculacion rechazada =
                solicitudVinculacionRepositorio.save(solicitud);

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Dispositivos,
                "Rechazar cambio",
                "Se rechazó la solicitud " + idSolicitud
                        + " del colaborador "
                        + solicitud.getColaborador().getNombres()
                        + " " + solicitud.getColaborador().getApellidos()
                        + ". Motivo: " + motivo
        );

        return rechazada;
    }

    // Desvincular un dispositivo desde el panel administrativo
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void desvincularDispositivo(int idDispositivo) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        DispositivoEmpleado dispositivo =
                dispositivoEmpleadoRepositorio.findById(idDispositivo)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El dispositivo no existe"
                        ));

        if (!dispositivo.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El dispositivo ya está desvinculado"
            );
        }

        dispositivo.setActivo(false);
        dispositivoEmpleadoRepositorio.save(dispositivo);

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Dispositivos,
                "Desvincular",
                "El supervisor desvinculó el dispositivo "
                        + idDispositivo
                        + " del colaborador "
                        + dispositivo.getColaborador().getNombres()
                        + " "
                        + dispositivo.getColaborador().getApellidos()
        );
    }
}
