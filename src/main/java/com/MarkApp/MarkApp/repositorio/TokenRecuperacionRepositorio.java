package com.MarkApp.MarkApp.repositorio;

import com.MarkApp.MarkApp.modelo.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperacionRepositorio extends JpaRepository<TokenRecuperacion,Integer> {

    Optional<TokenRecuperacion> findByTokenHash(String tokenHash);
}
