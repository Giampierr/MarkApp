package com.marcapp.app.feature.admin

import androidx.lifecycle.ViewModel
import com.marcapp.app.domain.repository.GestionRepository

class GestionViewModel(val repository: GestionRepository) : ViewModel() {
    val solicitudes = repository.solicitudes
    val trabajadores = repository.trabajadores
    val turnos = repository.turnos
    val feriados = repository.feriados
    val auditoria = repository.auditoria
    val tolerancia = repository.tolerancia
    val radioGeocerca = repository.radioGeocerca
}
