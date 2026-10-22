package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.enums.EstadoVinculacion;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.DispositivoEmpleado;
import com.MarkApp.MarkApp.modelo.SolicitudVinculacion;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.DispositivoEmpleadoRepositorio;
import com.MarkApp.MarkApp.repositorio.SolicitudVinculacionRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DispositivoServicio {
    private final DispositivoEmpleadoRepositorio dispositivoEmpleadoRepositorio;
    private final SolicitudVinculacionRepositorio solicitudVinculacionRepositorio;

    public DispositivoServicio(DispositivoEmpleadoRepositorio dispositivoEmpleadoRepositorio, SolicitudVinculacionRepositorio solicitudVinculacionRepositorio) {
        this.dispositivoEmpleadoRepositorio = dispositivoEmpleadoRepositorio;
        this.solicitudVinculacionRepositorio = solicitudVinculacionRepositorio;
    }

    //Metodo para vincular en el primer dispositivo a un empleado
    @Transactional
    public DispositivoEmpleado vincularPrimerDispositivo(Colaborador colaborador,DispositivoEmpleado dispositivoEmpleado){

        boolean tieneDispositivoActivo = dispositivoEmpleadoRepositorio.existsByColaborador_IdAndActiveTrue(colaborador.getId());

        if (tieneDispositivoActivo){
            throw new IllegalStateException("El colaborador ya tiene un dispositivo activo");
        }

        dispositivoEmpleado.setColaborador(colaborador);
        dispositivoEmpleado.setFechaVinculacion(LocalDateTime.now());
        dispositivoEmpleado.setActivo(true);

        return dispositivoEmpleadoRepositorio.save(dispositivoEmpleado);
    }

    //Metodo para cambio de dispositivo
    @Transactional
    public SolicitudVinculacion solicitarCambioDispositivo(Colaborador colaborador, DispositivoEmpleado nuevoDispositivo){

        int idColaborador = colaborador.getId();

        DispositivoEmpleado anterior = dispositivoEmpleadoRepositorio.findByColaborador_IdAndActivoTrue(idColaborador).orElseThrow(() -> new IllegalStateException("No existe un dispositivo activo para cambiar"));

        nuevoDispositivo.setColaborador(colaborador);
        nuevoDispositivo.setActivo(false);
        nuevoDispositivo.setFechaVinculacion(null);

        DispositivoEmpleado guardado = dispositivoEmpleadoRepositorio.save(nuevoDispositivo);

        SolicitudVinculacion solicitud = new SolicitudVinculacion();

        solicitud.setColaborador(colaborador);
        solicitud.setDispositivoAnterior(anterior);
        solicitud.setDispositivoSolicitado(guardado);
        solicitud.setEstado(EstadoVinculacion.Pendiente);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        return solicitud;
    }

    //Aprobar cambio de dispositivo
    @PreAuthorize("hasRole('Admin')")
    @Transactional
    public SolicitudVinculacion aprobarSolicitud(Long idSolicitud , Supervisor supervisor){
        SolicitudVinculacion solicitud = solicitudVinculacionRepositorio.findById(idSolicitud).orElseThrow(() -> new IllegalStateException("La solicitud no existe"));

        if (solicitud.getEstado() != EstadoVinculacion.Pendiente){
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        DispositivoEmpleado anterior = solicitud.getDispositivoAnterior();

        DispositivoEmpleado nuevo = solicitud.getDispositivoSolicitado();

        if (nuevo == null || nuevo.isActivo()){
            throw new IllegalStateException("El nuevo dispositivo no es valido");
        }

        if (anterior != null){
            anterior.setActivo(false);
            dispositivoEmpleadoRepositorio.save(anterior);
        }

        nuevo.setActivo(true);
        nuevo.setFechaVinculacion(LocalDateTime.now());
        dispositivoEmpleadoRepositorio.save(nuevo);

        solicitud.setSupervisor(supervisor);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setEstado(EstadoVinculacion.Aprobada);

        return solicitudVinculacionRepositorio.save(solicitud);

    }

    //Rechazar cambio de dispositivo

    @Transactional
    public SolicitudVinculacion rechazarSolicitud(Long idSolicitud,Supervisor supervisor, String motivo){
        SolicitudVinculacion solicitud = solicitudVinculacionRepositorio.findById(idSolicitud).orElseThrow(() -> new IllegalStateException("La solicitud no existe"));

        if (solicitud.getEstado() != EstadoVinculacion.Pendiente){
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        solicitud.getDispositivoSolicitado().setActivo(false);

        solicitud.setSupervisor(supervisor);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setEstado(EstadoVinculacion.Rechazada);
        solicitud.setMotivoRechazo(motivo);

        return solicitudVinculacionRepositorio.save(solicitud);
    }

    //Desvincular dspositivo
    @Transactional
    public void desvicularDispositivo(int idDispositivo){
        DispositivoEmpleado dispositivo = dispositivoEmpleadoRepositorio.findById(idDispositivo).orElseThrow(() -> new IllegalStateException("El dispositivo no existe"));

        if (!dispositivo.isActivo()){
            throw new IllegalStateException("El dispositivo ya está vinculado");
        }

        dispositivo.setActivo(false);
        dispositivoEmpleadoRepositorio.save(dispositivo);
    }

}
