package com.marcapp.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcapp.app.data.demo.*
import com.marcapp.app.data.remote.HttpAuthRepository
import com.marcapp.app.data.remote.RemoteAsistenciaRepository
import com.marcapp.app.feature.auth.SessionViewModel
import com.marcapp.app.feature.attendance.AttendanceViewModel
import com.marcapp.app.feature.admin.GestionViewModel

/** Composition root: replacements can implement interfaces without changing UI/use cases. */
object MarcAppContainer {
    val auth = HttpAuthRepository()
    val asistencia = RemoteAsistenciaRepository()
    val gestion = DemoGestionRepository()
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
            SessionViewModel::class.java -> SessionViewModel(auth)
            AttendanceViewModel::class.java -> AttendanceViewModel(asistencia)
            GestionViewModel::class.java -> GestionViewModel(gestion)
            else -> error("ViewModel desconocido: $modelClass")
        } as T
    }
}
