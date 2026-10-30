package com.marcapp.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcapp.app.domain.model.*
import com.marcapp.app.domain.repository.*
import com.marcapp.app.domain.usecase.IniciarSesionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(val cargando: Boolean = false, val error: String? = null)

class SessionViewModel(private val auth: AuthRepository) : ViewModel() {
    private val usuarioMutable = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = usuarioMutable
    private val loginMutable = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = loginMutable
    private val iniciar = IniciarSesionUseCase(auth)

    fun entrarDemo(rol: Rol) { loginMutable.value = LoginUiState(); usuarioMutable.value = auth.demo(rol) }
    fun entrar(correo: String, clave: String) {
        viewModelScope.launch {
            loginMutable.value = LoginUiState(cargando = true)
            when (val respuesta = iniciar(correo, clave)) {
                is ResultadoLogin.Correcto -> { usuarioMutable.value = respuesta.usuario; loginMutable.value = LoginUiState() }
                is ResultadoLogin.Error -> loginMutable.value = LoginUiState(error = respuesta.mensaje)
            }
        }
    }
    fun cerrarSesion() { auth.cerrar(); usuarioMutable.value = null; loginMutable.value = LoginUiState() }
}
