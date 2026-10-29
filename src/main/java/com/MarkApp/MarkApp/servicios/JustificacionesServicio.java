package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.JustificacionAtendidaDTO;
import com.MarkApp.MarkApp.dto.JustificacionDTO;
import com.MarkApp.MarkApp.dto.JustificacionListadoDTO;
import com.MarkApp.MarkApp.enums.EstadoJustificacion;
import com.MarkApp.MarkApp.enums.TipoAsistencia;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.Justificacion;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.JustificacionRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JustificacionesServicio {
    private ColaboradorRepositorio colaboradorRepositorio;
    private SupervisorRepositorio supervisorRepositorio;
    private JustificacionRepositorio justificacionRepositorio;
    private SupabaseStorageServicio supabaseStorageServicio;

    public JustificacionesServicio(ColaboradorRepositorio colaboradorRepositorio, SupervisorRepositorio supervisorRepositorio, JustificacionRepositorio justificacionRepositorio, SupabaseStorageServicio supabaseStorageServicio) {
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.supervisorRepositorio = supervisorRepositorio;
        this.justificacionRepositorio = justificacionRepositorio;
        this.supabaseStorageServicio = supabaseStorageServicio;
    }



    @Transactional
    public Justificacion agregarJustificacion(
            int id,
            TipoAsistencia tipoAsistencia,
            String mensajeColaborador,
            MultipartFile archivo
    ) throws IOException {

        Colaborador colaborador = colaboradorRepositorio
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Colaborador no encontrado"
                ));

        // Subir la evidencia a Supabase si se adjuntó un archivo.
        String rutaArchivo = null;

        if (archivo != null && !archivo.isEmpty()) {
            rutaArchivo = supabaseStorageServicio.subirArchivo(archivo);
        }

        // Crear la justificación.
        Justificacion justificacion = new Justificacion();

        justificacion.setColaborador(colaborador);
        justificacion.setHoraMensajeColaborador(LocalDateTime.now());
        justificacion.setTipoAsistencia(tipoAsistencia);
        justificacion.setMensajeColaborador(mensajeColaborador);
        justificacion.setArchivoColaborador(rutaArchivo);
        justificacion.setEstadoJustificacion(EstadoJustificacion.Pendiente);

        return justificacionRepositorio.save(justificacion);
    }


    @Transactional
    public Justificacion atenderJustificacion(int idSupervisor, JustificacionAtendidaDTO justificacionDto){
        Justificacion justificacion = justificacionRepositorio.findById(justificacionDto.idJustificacion()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Justificacion no encontrado"));

        Supervisor supervisor = supervisorRepositorio.findById(idSupervisor).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Supervisor no encontrado"));

        if (justificacion.getEstadoJustificacion() != EstadoJustificacion.Pendiente){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Esta justificacion ya fue atendida");
        }

        justificacion.setSupervisor(supervisor);
        justificacion.setRespuestaSupervisor(justificacionDto.respuestaSupervisor());
        justificacion.setHoraRespuestaSupervisor(LocalDateTime.now());
        switch (justificacionDto.estadoJustificacion()){
            case Atendido -> justificacion.setEstadoJustificacion(EstadoJustificacion.Atendido);
            case Rechazado -> justificacion.setEstadoJustificacion(EstadoJustificacion.Rechazado);
        }

        return justificacionRepositorio.save(justificacion);
    }

    @Transactional
    public List<JustificacionListadoDTO> listarParaAdministracion(){
        return justificacionRepositorio.listarParaAdministracion();
    }

}
