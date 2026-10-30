package com.marcapp.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.marcapp.app.core.designsystem.MarcAppTheme
import com.marcapp.app.di.MarcAppContainer
import com.marcapp.app.feature.admin.GestionViewModel
import com.marcapp.app.feature.attendance.AttendanceViewModel
import com.marcapp.app.feature.auth.SessionViewModel
import com.marcapp.app.feature.shell.MarcAppRoot

class MainActivity : FragmentActivity() {
    private val sesion: SessionViewModel by viewModels { MarcAppContainer.factory }
    private val asistencia: AttendanceViewModel by viewModels { MarcAppContainer.factory }
    private val gestion: GestionViewModel by viewModels { MarcAppContainer.factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MarcAppTheme { MarcAppRoot(sesion, asistencia, gestion) } }
    }
}
