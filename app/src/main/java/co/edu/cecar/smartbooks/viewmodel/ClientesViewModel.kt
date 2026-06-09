package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.model.*
import co.edu.cecar.smartbooks.data.repository.ClientesRepository
import kotlinx.coroutines.launch
import java.text.Normalizer

class ClientesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ClientesRepository()
    private val context = application.applicationContext


    var uiState by mutableStateOf<ClientesUiState>(ClientesUiState.Loading)
        private set


    private var listaClientesCompleta: List<ClienteResponse> = emptyList()

    private val _clienteSeleccionadoParaEditar = mutableStateOf<ClienteResponse?>(null)
    var clienteSeleccionadoParaEditar: ClienteResponse?
        get() = _clienteSeleccionadoParaEditar.value
        set(value) {
            _clienteSeleccionadoParaEditar.value = value
            dialogErrorMessage = null
        }

    private val _mostrarDialogoCrear = mutableStateOf(false)
    var mostrarDialogoCrear: Boolean
        get() = _mostrarDialogoCrear.value
        set(value) {
            _mostrarDialogoCrear.value = value
            dialogErrorMessage = null
        }

    var dialogErrorMessage by mutableStateOf<String?>(null)

    private fun quitarAcentos(texto: String): String {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("""\p{InCombiningDiacriticalMarks}+""".toRegex(), "")
    }

    fun cargarClientes(nombres: String? = null) {
        viewModelScope.launch {
            uiState = ClientesUiState.Loading
            try {
                val token = TokenManager.getToken(context) ?: ""
                val lista = repository.obtenerClientes(token, nombres)

                if (nombres == null) {
                    listaClientesCompleta = lista
                }

                uiState = ClientesUiState.Success(lista)
            } catch (e: Exception) {
                uiState = ClientesUiState.Error(e.localizedMessage ?: "Error al conectar con el servidor")
            }
        }
    }

    fun buscarClientes(nombre: String) {
        val queryLimpio = nombre.trim()

        if (queryLimpio.isEmpty()) {
            uiState = ClientesUiState.Success(listaClientesCompleta)
            return
        }

        val esNumero = queryLimpio.all { it.isDigit() }

        if (esNumero) {

            val filtrados = listaClientesCompleta.filter { cliente ->
                cliente.identificacion.contains(queryLimpio)
            }
            uiState = ClientesUiState.Success(filtrados)
        } else {
            cargarClientes(nombres = queryLimpio)
        }
    }

    fun guardarNuevoCliente(request: ClienteCreateRequest) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context) ?: ""
                dialogErrorMessage = null

                if (request.identificacion.isBlank() || request.nombres.isBlank() ||
                    request.email.isBlank() || request.celular.isBlank() ||
                    request.fechaNacimiento.isBlank()
                ) {
                    dialogErrorMessage = "Todos los campos son obligatorios"
                    return@launch
                }

                val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")
                if (!regexFecha.matches(request.fechaNacimiento)) {
                    dialogErrorMessage = "La fecha debe estar en formato YYYY-MM-DD"
                    return@launch
                }

                val requestLimpio = ClienteCreateRequest(
                    identificacion = request.identificacion.trim(),
                    nombres = quitarAcentos(request.nombres.trim().uppercase()),
                    email = request.email.trim(),
                    celular = request.celular.trim(),
                    fechaNacimiento = request.fechaNacimiento.trim()
                )

                val (exito, errorMsg) = repository.crearCliente(token, requestLimpio)

                if (exito) {
                    mostrarDialogoCrear = false
                    cargarClientes()
                } else {
                    dialogErrorMessage = errorMsg ?: "Error desconocido al crear cliente"
                }
            } catch (e: Exception) {
                dialogErrorMessage = "Excepción al crear cliente: ${e.message}"
            }
        }
    }

    fun modificarCliente(identificacion: String, request: ClienteUpdateRequest) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context) ?: ""
                dialogErrorMessage = null

                if (request.nombres.isBlank() || request.email.isBlank() ||
                    request.celular.isBlank() || request.fechaNacimiento.isBlank()
                ) {
                    dialogErrorMessage = "Todos los campos son obligatorios"
                    return@launch
                }

                val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")
                if (!regexFecha.matches(request.fechaNacimiento)) {
                    dialogErrorMessage = "La fecha debe estar en formato YYYY-MM-DD"
                    return@launch
                }

                val requestLimpio = ClienteUpdateRequest(
                    nombres = quitarAcentos(request.nombres.trim().uppercase()),
                    email = request.email.trim(),
                    celular = request.celular.trim(),
                    fechaNacimiento = request.fechaNacimiento.trim()
                )

                val (exito, errorMsg) = repository.actualizarCliente(token, identificacion, requestLimpio)

                if (exito) {
                    clienteSeleccionadoParaEditar = null
                    cargarClientes()
                } else {
                    dialogErrorMessage = errorMsg ?: "Error al actualizar cliente"
                }
            } catch (e: Exception) {
                dialogErrorMessage = "Excepción al actualizar cliente: ${e.message}"
            }
        }
    }
}

sealed interface ClientesUiState {
    object Loading : ClientesUiState
    data class Success(val clientes: List<ClienteResponse>) : ClientesUiState
    data class Error(val message: String) : ClientesUiState
}