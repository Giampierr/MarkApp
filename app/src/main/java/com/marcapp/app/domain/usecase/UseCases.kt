package com.marcapp.app.domain.usecase

import com.marcapp.app.domain.model.*
import com.marcapp.app.domain.repository.*

class IniciarSesionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(correo: String, clave: String): ResultadoLogin {
        if (!correo.contains('@') || clave.isBlank())
            return ResultadoLogin.Error("Ingresa un correo válido y contraseña.")
        return repository.iniciar(correo.trim(), clave)
    }
}

class RegistrarMarcacionUseCase(private val repository: AsistenciaRepository) {
    suspend operator fun invoke(tipo: TipoMarca, usuario: Usuario, latitud: Double?, longitud: Double?): String =
        repository.marcar(tipo, usuario, latitud, longitud)
}
