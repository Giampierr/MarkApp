package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.modelo.Auditoria;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.AuditoriaRepositorio;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaServicio {
    private final AuditoriaRepositorio auditoriaRepositorio;

    public AuditoriaServicio(AuditoriaRepositorio auditoriaRepositorio) {
        this.auditoriaRepositorio = auditoriaRepositorio;
    }

    public Auditoria crearAuditoria(Supervisor supervisor, Modulos modulo , String accion, String detalle){
        Auditoria auditoria = new Auditoria();

        auditoria.setFechaYHora(LocalDateTime.now());
        auditoria.setSupervisor(supervisor);
        auditoria.setModulo(modulo);
        auditoria.setAccion(accion);
        auditoria.setDetalle(detalle);

        return auditoriaRepositorio.save(auditoria);
    }
}
