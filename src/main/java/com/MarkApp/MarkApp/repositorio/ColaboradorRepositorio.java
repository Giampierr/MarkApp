package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColaboradorRepositorio extends JpaRepository<Colaborador,Integer> {

    Optional<Colaborador> findByCorreo(String correo);
}
