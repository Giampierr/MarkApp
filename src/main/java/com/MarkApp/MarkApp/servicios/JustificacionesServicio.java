
package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.JustificacionAtendidaDTO;
import com.MarkApp.MarkApp.dto.JustificacionListadoDTO;
import com.MarkApp.MarkApp.enums.EstadoJustificacion;
import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.enums.TipoAsistencia;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.Justificacion;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.JustificacionRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import com.MarkApp.MarkApp.util.ObtenerIdUsuarioAutenticado;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JustificacionesServicio {

    private final ColaboradorRepositorio colaboradorRepositorio;
    private final SupervisorRepositorio supervisorRepositorio;
    private final JustificacionRepositorio justificacionRepositorio;
    private final SupabaseStorageServicio supabaseStorageServicio;
    private final AuditoriaServicio auditoriaServicio;

    public JustificacionesServicio(
            ColaboradorRepositorio colaboradorRepositorio,
            SupervisorRepositorio supervisorRepositorio,
            JustificacionRepositorio justificacionRepositorio,
            SupabaseStorageServicio supabaseStorageServicio,
            AuditoriaServicio auditoriaServicio) {

        this.colaboradorRepositorio = colaboradorRepositorio;
        this.supervisorRepositorio = supervisorRepositorio;
        this.justificacionRepositorio = justificacionRepositorio;
        this.supabaseStorageServicio = supabaseStorageServicio;
        this.auditoriaServicio = auditoriaServicio;
    }

    // Obtener al supervisor autenticado
    private Supervisor obtenerSupervisorAutenticado() {

        int idSupervisor =
                ObtenerIdUsuarioAutenticado.obtenerIdUsuarioAutenticado();

        return supervisorRepositorio.findById(idSupervisor)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El usuario autenticado no es un supervisor"
                ));
    }

    // Registrar una justificación enviada por un colaborador
    @Transactional
    public Justificacion agregarJustificacion(
            int id,
            TipoAsistencia tipoAsistencia,
            String mensajeColaborador,
            MultipartFile archivo
    ) throws IOException {

        Colaborador colaborador = colaboradorRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Colaborador no encontrado"
                ));

        // Subir la evidencia a Supabase si se adjuntó un archivo
        String rutaArchivo = null;

        if (archivo != null && !archivo.isEmpty()) {
            rutaArchivo = supabaseStorageServicio.subirArchivo(archivo);
        }

        Justificacion justificacion = new Justificacion();

        justificacion.setColaborador(colaborador);
        justificacion.setHoraMensajeColaborador(LocalDateTime.now());
        justificacion.setTipoAsistencia(tipoAsistencia);
        justificacion.setMensajeColaborador(mensajeColaborador);
        justificacion.setArchivoColaborador(rutaArchivo);
        justificacion.setEstadoJustificacion(EstadoJustificacion.Pendiente);

        return justificacionRepositorio.save(justificacion);
    }

    // Atender una justificación
    @Transactional
    public Justificacion atenderJustificacion(
            JustificacionAtendidaDTO justificacionDto) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        Justificacion justificacion =
                justificacionRepositorio.findById(
                        justificacionDto.idJustificacion()
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Justificación no encontrada"
                ));

        if (justificacion.getEstadoJustificacion()
                != EstadoJustificacion.Pendiente) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta justificación ya fue atendida"
            );
        }

        EstadoJustificacion nuevoEstado =
                justificacionDto.estadoJustificacion();

        if (nuevoEstado != EstadoJustificacion.Atendido
                && nuevoEstado != EstadoJustificacion.Rechazado) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado debe ser Atendido o Rechazado"
            );
        }

        justificacion.setSupervisor(supervisor);
        justificacion.setRespuestaSupervisor(
                justificacionDto.respuestaSupervisor()
        );
        justificacion.setHoraRespuestaSupervisor(LocalDateTime.now());
        justificacion.setEstadoJustificacion(nuevoEstado);

        Justificacion guardada =
                justificacionRepositorio.save(justificacion);

        String accion = nuevoEstado == EstadoJustificacion.Atendido
                ? "Aprobar"
                : "Rechazar";

        String detalle = "El supervisor atendió la justificación "
                + guardada.getIdJustificacion()
                + " del colaborador "
                + guardada.getColaborador().getNombres()
                + " "
                + guardada.getColaborador().getApellidos()
                + ". Resultado: "
                + nuevoEstado;

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Justificaciones,
                accion,
                detalle
        );

        return guardada;
    }

    // Listar justificaciones para el panel administrativo
    @Transactional(readOnly = true)
    public List<JustificacionListadoDTO> listarParaAdministracion() {
        return justificacionRepositorio.listarParaAdministracion();
    }
}
