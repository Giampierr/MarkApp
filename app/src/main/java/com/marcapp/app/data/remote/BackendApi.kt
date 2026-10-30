package com.marcapp.app.data.remote

import com.marcapp.app.BuildConfig
import com.marcapp.app.domain.model.*
import com.marcapp.app.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// URL pública del backend. NO se distribuyen contraseñas de Supabase ni secretos JWT en la APK.
data class LoginRequest(val correo: String, val password: String)
data class LoginResponse(val token: String?, val tipoUsuario: String?, val rol: String?)
data class PerfilResponse(val idUsuario: Int, val nombre: String, val correo: String, val rol: String)
data class MarcaRequest(val tipo: String, val latitud: Double, val longitud: Double)
data class MarcaResponse(val tipo: String, val fechaHora: String, val estado: String)

interface BackendApi {
    @POST("api/auth/login/colaborador")
    suspend fun loginTrabajador(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/login/supervisor")
    suspend fun loginSupervisor(@Body request: LoginRequest): LoginResponse

    @GET("api/marcapp/me")
    suspend fun perfil(@Header("Authorization") bearer: String): PerfilResponse

    @GET("api/marcapp/marcaciones")
    suspend fun historial(@Header("Authorization") bearer: String, @Query("dias") dias: Int = 7): List<MarcaResponse>

    @POST("api/marcapp/marcaciones")
    suspend fun marcar(@Header("Authorization") bearer: String, @Body marca: MarcaRequest): MarcaResponse
}

/** Token temporal en memoria; se pierde al cerrar completamente la aplicación. */
object SesionRemota {
    var token: String? = null
        private set
    fun iniciar(valor: String) { token = valor }
    fun cerrar() { token = null }
    fun bearer(): String? = token?.let { "Bearer $it" }
}

object MarcAppHttp {
    private val cliente = OkHttpClient.Builder()
        // Render Free puede tardar más de 50s en despertar.
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .callTimeout(140, TimeUnit.SECONDS)
        .build()
    val api: BackendApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(BackendApi::class.java)
}

class HttpAuthRepository(private val api: BackendApi = MarcAppHttp.api) : AuthRepository {
    override fun demo(rol: Rol): Usuario {
        SesionRemota.cerrar()
        return Usuario(
            if (rol == Rol.TRABAJADOR) "María Torres" else "Carlos Ramírez",
            if (rol == Rol.TRABAJADOR) "demo@buypal.com.pe" else "demo-admin@buypal.com.pe", rol)
    }

    override fun cerrar() { SesionRemota.cerrar() }

    override suspend fun iniciar(correo: String, clave: String): ResultadoLogin = withContext(Dispatchers.IO) {
        SesionRemota.cerrar()
        try {
            val peticion = LoginRequest(correo, clave)
            val respuesta = try {
                api.loginTrabajador(peticion)
            } catch (e: HttpException) {
                if (e.code() == 401) api.loginSupervisor(peticion) else throw e
            }
            val token = respuesta.token
            if (token.isNullOrBlank()) return@withContext ResultadoLogin.Error("El servidor no devolvió un token.")
            // El perfil viene de un endpoint protegido y el backend valida la firma del token.
            val perfil = try { api.perfil("Bearer $token") } catch (ex: HttpException) {
                if (ex.code() == 403 || ex.code() == 404) return@withContext ResultadoLogin.Error(
                    "Actualiza primero el backend de Render para habilitar /api/marcapp/me."
                )
                throw ex
            }
            SesionRemota.iniciar(token)
            ResultadoLogin.Correcto(Usuario(
                nombre = perfil.nombre,
                correo = perfil.correo,
                rol = if (perfil.rol == "ADMINISTRADOR") Rol.ADMINISTRADOR else Rol.TRABAJADOR,
                idColaborador = if (perfil.rol == "TRABAJADOR") perfil.idUsuario else null,
                esDemo = false
            ))
        } catch (e: HttpException) {
            val mensaje = when (e.code()) {
                401 -> "Correo o contraseña incorrectos, o sesión expirada."
                403 -> "Acceso no autorizado. Comprueba que desplegaste el backend actualizado."
                404 -> "Ruta no encontrada. Actualiza el backend en Render."
                502, 503, 504 -> "Servidor temporalmente no disponible. Prueba de nuevo."
                else -> "Error HTTP ${e.code()} al iniciar sesión."
            }
            ResultadoLogin.Error(mensaje)
        } catch (_: Exception) {
            ResultadoLogin.Error("No se pudo contactar al servidor. Comprueba Internet y Render.")
        }
    }
}
