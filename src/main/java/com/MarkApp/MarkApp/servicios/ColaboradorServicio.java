
package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.ColaboradorCambiosDTO;
import com.MarkApp.MarkApp.dto.ColaboradorListadoDTO;
import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import com.MarkApp.MarkApp.util.ObtenerIdUsuarioAutenticado;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColaboradorServicio {

    private final ColaboradorRepositorio colaboradorRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final SupervisorRepositorio supervisorRepositorio;
    private final AuditoriaServicio auditoriaServicio;

    public ColaboradorServicio(
            ColaboradorRepositorio colaboradorRepositorio,
            PasswordEncoder passwordEncoder,
            SupervisorRepositorio supervisorRepositorio,
            AuditoriaServicio auditoriaServicio) {

        this.colaboradorRepositorio = colaboradorRepositorio;
        this.passwordEncoder = passwordEncoder;
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
    public Colaborador registrar(Colaborador colaborador) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        colaborador.setPassword(
                passwordEncoder.encode(colaborador.getPassword())
        );

        Colaborador guardado = colaboradorRepositorio.save(colaborador);

        String detalle = "Se registró al colaborador: "
                + guardado.getNombres() + " "
                + guardado.getApellidos()
                + " (ID: " + guardado.getId() + ")";

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Colaboradores,
                "Registrar",
                detalle
        );

        return guardado;
    }

    @Transactional
    public Colaborador actualizarParcialmente(
            int id,
            ColaboradorCambiosDTO cambios) {

        Supervisor supervisor = obtenerSupervisorAutenticado();

        Colaborador colaborador = colaboradorRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Colaborador no encontrado"
                ));

        List<String> modificaciones = new ArrayList<>();

        if (cambios.nombres() != null
                && !cambios.nombres().equals(colaborador.getNombres())) {
            modificaciones.add("nombres");
            colaborador.setNombres(cambios.nombres());
        }

        if (cambios.apellidos() != null
                && !cambios.apellidos().equals(colaborador.getApellidos())) {
            modificaciones.add("apellidos");
            colaborador.setApellidos(cambios.apellidos());
        }

        if (cambios.correo() != null
                && !cambios.correo().equals(colaborador.getCorreo())) {
            modificaciones.add("correo");
            colaborador.setCorreo(cambios.correo());
        }

        if (cambios.cargo() != null
                && !cambios.cargo().equals(colaborador.getCargo())) {
            modificaciones.add("cargo");
            colaborador.setCargo(cambios.cargo());
        }

        if (modificaciones.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se enviaron cambios nuevos para el colaborador"
            );
        }

        Colaborador actualizado = colaboradorRepositorio.save(colaborador);

        String detalle = "Se actualizó al colaborador: "
                + actualizado.getNombres() + " "
                + actualizado.getApellidos()
                + " (ID: " + actualizado.getId() + "). "
                + "Campos modificados: "
                + String.join(", ", modificaciones);

        auditoriaServicio.crearAuditoria(
                supervisor,
                Modulos.Colaboradores,
                "Actualizar",
                detalle
        );

        return actualizado;
    }

    @Transactional(readOnly = true)
    public List<ColaboradorListadoDTO> listarParaAdministracion() {
        return colaboradorRepositorio.listarParaAdministracion();
    }
}
