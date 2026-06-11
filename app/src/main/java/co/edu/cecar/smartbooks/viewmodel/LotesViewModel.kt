package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.repository.LotesRepository
import co.edu.cecar.smartbooks.data.model.LoteResponse
import kotlinx.coroutines.launch

class LotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LotesRepository()
    private val context = application.applicationContext

    var lotes = mutableStateOf<List<LoteResponse>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var listErrorMessage = mutableStateOf<String?>(null)
        private set

    fun cargarLotes() {
        viewModelScope.launch {
            isLoading.value = true
            listErrorMessage.value = null
            try {
                val token = TokenManager.getToken(context) ?: ""
                lotes.value = repository.obtenerLotes(token)
            } catch (e: Exception) {
                listErrorMessage.value = "Error al cargar lotes: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun crearLote(numero: Int, onResult: (exito: Boolean, errorMsg: String?) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val token = TokenManager.getToken(context) ?: ""
                val exito = repository.crearLote(token, numero)
                if (exito) {
                    cargarLotes()
                    onResult(true, null)
                } else {
                    onResult(false, "El lote '$numero' ya se encuentra registrado en el sistema.")
                }
            } catch (e: Exception) {
                onResult(false, "No se pudo conectar con el servidor: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }
}