package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.data.model.IngresoCreate
import co.edu.cecar.smartbooks.data.model.IngresoResponse
import co.edu.cecar.smartbooks.data.model.LibroResponse
import co.edu.cecar.smartbooks.data.repository.IngresosRepository
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

sealed interface IngresosUiState {
    object Loading : IngresosUiState
    data class Success(val lista: List<IngresoResponse>) : IngresosUiState
    data class Error(val message: String) : IngresosUiState
}

class IngresosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = IngresosRepository()
    private val client = co.edu.cecar.smartbooks.core.network.HttpClientProvider.client

    var uiState: IngresosUiState by mutableStateOf(IngresosUiState.Loading)
        private set

    var librosDisponibles by mutableStateOf<List<LibroResponse>>(emptyList())
        private set

    var libroSeleccionado by mutableStateOf<LibroResponse?>(null)
    var loteInput by mutableStateOf("")
    var unidadesInput by mutableStateOf("")
    var valorCompraInput by mutableStateOf("")
    var valorVentaInput by mutableStateOf("")

    var mensajeErrorFormulario by mutableStateOf<String?>(null)
    var isGuardando by mutableStateOf(false)

    fun cargarIngresos() {
        uiState = IngresosUiState.Loading
        viewModelScope.launch {
            val librosDeferred = async { cargarLibrosParaSeleccion() }
            librosDeferred.await()

            repository.obtenerIngresos().fold(
                onSuccess = { lista ->
                    uiState = IngresosUiState.Success(lista)
                },
                onFailure = { error ->
                    uiState = IngresosUiState.Error(error.localizedMessage ?: "Error de red")
                }
            )
        }
    }

    private suspend fun cargarLibrosParaSeleccion() {
        try {
            val response = client.get("https://api.smartbooks.cecar.cloud/api/Libros")
            if (response.status.isSuccess()) {
                librosDisponibles = response.body<List<LibroResponse>>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun guardarNuevoIngreso(onSuccess: () -> Unit) {
        val lId = libroSeleccionado?.id
        val lt = loteInput.toIntOrNull()
        val unid = unidadesInput.toIntOrNull()
        val vCompra = valorCompraInput.toDoubleOrNull()
        val vVenta = valorVentaInput.toDoubleOrNull()

        if (lId == null || lt == null || unid == null || vCompra == null || vVenta == null) {
            mensajeErrorFormulario = "Por favor, completa todos los campos correctamente."
            return
        }

        mensajeErrorFormulario = null
        isGuardando = true

        viewModelScope.launch {
            val nuevoIngresoDto = IngresoCreate(
                libroId = lId,
                unidades = unid,
                lote = lt,
                valorCompra = vCompra,
                valorVentaPublico = vVenta
            )

            repository.registrarIngreso(nuevoIngresoDto).fold(
                onSuccess = {
                    isGuardando = false
                    limpiarFormulario()
                    cargarIngresos()
                    onSuccess()
                },
                onFailure = { error ->
                    mensajeErrorFormulario = error.localizedMessage
                    isGuardando = false
                }
            )
        }
    }

    private fun limpiarFormulario() {
        libroSeleccionado = null
        loteInput = ""
        unidadesInput = ""
        valorCompraInput = ""
        valorVentaInput = ""
        mensajeErrorFormulario = null
    }
}