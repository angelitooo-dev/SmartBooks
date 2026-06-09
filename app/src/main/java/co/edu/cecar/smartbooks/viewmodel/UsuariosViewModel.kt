package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.remote.RegisterUsuarioDto
import co.edu.cecar.smartbooks.data.remote.UpdateUsuarioDto
import co.edu.cecar.smartbooks.data.remote.UsuarioResponse
import co.edu.cecar.smartbooks.data.repository.UsuariosRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull

class UsuariosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UsuariosRepository()
    private val context = application.applicationContext

    var listaUsuarios = mutableStateOf<List<UsuarioResponse>>(emptyList())
    var usuarioDetalle = mutableStateOf<UsuarioResponse?>(null)
    var perfilAutenticado = mutableStateOf<UsuarioResponse?>(null)

    // ESTADOS DE CARGA
    var isLoading = mutableStateOf(false)
    var isLoadingPerfil = mutableStateOf(false) // 👈 ESTO CORRIGE EL ERROR DE UNRESOLVED REFERENCE

    var mensajeError = mutableStateOf<String?>(null)
    var operacionExitosa = mutableStateOf(false)

    fun obtenerTextoRol(usuario: UsuarioResponse?): String {
        if (usuario == null) return "Vendedor"
        val primitivo = usuario.rol?.jsonPrimitive ?: return "Vendedor"

        return if (primitivo.isString) {
            val texto = primitivo.content.trim()
            if (texto.equals("Admin", ignoreCase = true) || texto == "1") "Admin" else "Vendedor"
        } else {
            if (primitivo.intOrNull == 1) "Admin" else "Vendedor"
        }
    }

    fun obtenerIdRol(usuario: UsuarioResponse): Int {
        val primitivo = usuario.rol?.jsonPrimitive ?: return 0
        return if (primitivo.isString) {
            val texto = primitivo.content.trim()
            if (texto.equals("Admin", ignoreCase = true) || texto == "1") 1 else 0
        } else {
            if (primitivo.intOrNull == 1) 1 else 0
        }
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            isLoading.value = true
            mensajeError.value = null
            try {
                val token = TokenManager.getToken(context)
                if (!token.isNullOrBlank() && token != "null") {
                    listaUsuarios.value = repository.obtenerUsuarios(token = token, nombres = null, rol = null)
                } else {
                    mensajeError.value = "Token inválido. Inicie sesión nuevamente."
                }
            } catch (e: Exception) {
                mensajeError.value = "Detalle del error: ${e.localizedMessage ?: "Error de red"}"
                Log.e("API_ERROR", "Fallo al cargar", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun registrarNuevoUsuario(dto: RegisterUsuarioDto) {
        viewModelScope.launch {
            isLoading.value = true
            mensajeError.value = null
            try {
                val token = TokenManager.getToken(context)
                if (!token.isNullOrBlank() && token != "null") {
                    val exito = repository.registrarUsuario(token, dto)
                    if (exito) operacionExitosa.value = true
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Fallo al registrar", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun editarUsuario(id: Int, dto: UpdateUsuarioDto) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val token = TokenManager.getToken(context)
                if (!token.isNullOrBlank() && token != "null") {
                    val exito = repository.actualizarUsuario(token, id, dto)
                    if (exito) operacionExitosa.value = true
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al editar", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun cambiarEstadoUsuario(id: Int) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (!token.isNullOrBlank() && token != "null") {
                    val exito = repository.cambiarEstadoUsuario(token, id)
                    if (exito) cargarUsuarios()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Fallo estado", e)
            }
        }
    }

    fun cargarPerfilUsuarioAutenticado() {
        viewModelScope.launch {
            isLoadingPerfil.value = true // Enciende el esqueleto/loading circular
            try {
                val token = TokenManager.getToken(context)
                if (!token.isNullOrBlank() && token != "null") {
                    perfilAutenticado.value = repository.obtenerPerfilAutenticado(token)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Fallo perfil", e)
            } finally {
                isLoadingPerfil.value = false // Apaga el loading
            }
        }
    }
}