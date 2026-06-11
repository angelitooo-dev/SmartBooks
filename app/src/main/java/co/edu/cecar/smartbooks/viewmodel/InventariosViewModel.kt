package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.remote.InventarioResponse
import co.edu.cecar.smartbooks.data.repository.InventariosRepository
import kotlinx.coroutines.launch

class InventariosViewModel(application: Application) : AndroidViewModel(application) {

    private val inventariosRepository = InventariosRepository()

    var listaInventario = mutableStateOf<List<InventarioResponse>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var listErrorMessage = mutableStateOf<String?>(null)
        private set

    var totalLibros = mutableStateOf(0)
        private set

    var bajoStock = mutableStateOf(0)
        private set

    var stockTotalUnits = mutableStateOf(0)
        private set

    fun cargarInventario(loteFiltro: Int? = null) {
        viewModelScope.launch {
            isLoading.value = true
            listErrorMessage.value = null
            try {
                val context = getApplication<Application>().applicationContext
                val token = TokenManager.getToken(context) ?: ""

                val resultadoCompleto = inventariosRepository.obtenerDatosInventario(token, loteFiltro)
                listaInventario.value = resultadoCompleto

                totalLibros.value = resultadoCompleto.size
                bajoStock.value = resultadoCompleto.count { (it.stockDisponible ?: 0) <= 5 }
                stockTotalUnits.value = resultadoCompleto.sumOf { it.stockDisponible ?: 0 }

            } catch (e: Exception) {
                listErrorMessage.value = "Error: ${e.localizedMessage}"
                Log.e("API_INVENTARIO", "Error crítico en ViewModel", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}