package com.marcapp.app

import com.marcapp.app.data.demo.DemoAsistenciaRepository
import com.marcapp.app.domain.model.Rol
import com.marcapp.app.domain.model.TipoMarca
import com.marcapp.app.domain.model.Usuario
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoAsistenciaRepositoryTest {
    private val usuario = Usuario("María Torres", "demo@buypal.com.pe", Rol.TRABAJADOR)

    @Test fun `no acepta salida antes de ingreso`() = runBlocking {
        val repo = DemoAsistenciaRepository()
        val resultado = repo.marcar(TipoMarca.SALIDA, usuario)
        assertTrue(resultado.contains("No puedes"))
        assertEquals(0, repo.marcaciones.value.size)
    }

    @Test fun `evita duplicar ingreso`() = runBlocking {
        val repo = DemoAsistenciaRepository()
        repo.marcar(TipoMarca.ENTRADA, usuario)
        val duplicada = repo.marcar(TipoMarca.ENTRADA, usuario)
        assertTrue(duplicada.contains("ya se registró"))
        assertEquals(1, repo.marcaciones.value.size)
    }

    @Test fun `permite secuencia completa de jornada`() = runBlocking {
        val repo = DemoAsistenciaRepository()
        for (tipo in TipoMarca.entries) repo.marcar(tipo, usuario)
        assertEquals(4, repo.marcaciones.value.size)
    }
}
