package com.marcapp.app.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcapp.app.domain.model.*
import com.marcapp.app.domain.repository.AsistenciaRepository
import com.marcapp.app.domain.usecase.RegistrarMarcacionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AsistenciaRepository) : ViewModel() {
    val marcaciones = repository.marcaciones
    private val mensajeMutable = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = mensajeMutable
    private val procesoMutable = MutableStateFlow(false)
    val enProceso: StateFlow<Boolean> = procesoMutable
    private val registrar = RegistrarMarcacionUseCase(repository)

    fun cargar(usuario: Usuario) {
        viewModelScope.launch {
            mensajeMutable.value = null
            val error = repository.cargar(usuario)
            if (error != null) mensajeMutable.value = error
        }
    }

    fun marcar(tipo: TipoMarca, usuario: Usuario, latitud: Double? = null, longitud: Double? = null) {
        if (procesoMutable.value) return
        viewModelScope.launch {
            procesoMutable.value = true
            try { mensajeMutable.value = registrar(tipo, usuario, latitud, longitud) }
            finally { procesoMutable.value = false }
        }
    }
    fun limpiarMensaje() { mensajeMutable.value = null }
}
