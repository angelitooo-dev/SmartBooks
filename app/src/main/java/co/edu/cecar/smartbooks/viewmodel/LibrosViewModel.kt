package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.model.LibroResponse
import co.edu.cecar.smartbooks.data.model.LibroUpdateRequest
import co.edu.cecar.smartbooks.data.model.LibroCreateRequest
import co.edu.cecar.smartbooks.data.repository.LibrosRepository
import kotlinx.coroutines.launch

class LibrosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LibrosRepository()
    private val context = application.applicationContext

    var uiState by mutableStateOf<LibrosUiState>(LibrosUiState.Loading)
        private set

    var libroSeleccionadoParaEditar by mutableStateOf<LibroResponse?>(null)
    var mostrarDialogoCrear by mutableStateOf(false)

    fun cargarLibros() {
        viewModelScope.launch {
            uiState = LibrosUiState.Loading
            try {
                val token = TokenManager.getToken(context) ?: ""
                val lista = repository.obtenerLibros(token)
                uiState = LibrosUiState.Success(lista)
            } catch (e: Exception) {
                uiState =
                    LibrosUiState.Error(e.localizedMessage ?: "Error al conectar con el servidor")
            }
        }
    }

    fun modificarLibro(id: Int, nombre: String, nivel: String, tipo: String, edicion: String) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context) ?: ""
                val tipoEntero = tipo.toIntOrNull() ?: if (tipo.equals(
                        "StudentsBook",
                        ignoreCase = true
                    )
                ) 1 else 1

                val request = LibroUpdateRequest(
                    id = id,
                    nombre = nombre,
                    nivel = nivel,
                    tipo = tipoEntero,
                    edicion = edicion
                )
                val exito = repository.actualizarLibro(token, id, request)
                if (exito) {
                    libroSeleccionadoParaEditar = null
                    cargarLibros()
                } else {
                    uiState = LibrosUiState.Error("El servidor rechazó la actualización (PUT)")
                }
            } catch (e: Exception) {
                uiState = LibrosUiState.Error(e.localizedMessage ?: "Fallo al actualizar libro")
            }
        }
    }


    fun guardarNuevoLibro(
        nombre: String,
        nivel: String,
        tipo: String,
        edicion: String,
        unidades: Int,
        lote: Int,
        compra: Int,
        venta: Int
    ) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context) ?: ""
                val tipoEntero = tipo.toIntOrNull() ?: 1

                val request = LibroCreateRequest(
                    nombre = nombre.trim(),
                    nivel = nivel.trim(),
                    tipo = tipoEntero,
                    edicion = edicion.trim(),
                    unidades = unidades,
                    lote = lote,
                    valorCompra = compra,
                    valorVentaPublico = venta
                )

                val exito = repository.crearLibro(token, request)
                if (exito) {
                    cargarLibros()
                    mostrarDialogoCrear = false
                } else {
                    uiState = LibrosUiState.Error("Error al crear libro")
                }
            } catch (e: Exception) {
                uiState = LibrosUiState.Error("Excepción al crear libro: ${e.message}")
            }
        }
    }


}
    sealed interface LibrosUiState {
    object Loading : LibrosUiState
    data class Success(val libros: List<LibroResponse>) : LibrosUiState
    data class Error(val message: String) : LibrosUiState
}