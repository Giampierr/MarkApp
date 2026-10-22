package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisorRepositorio extends JpaRepository<Supervisor,Integer> {

    Optional<Supervisor> findByCorreo(String correo);
}
