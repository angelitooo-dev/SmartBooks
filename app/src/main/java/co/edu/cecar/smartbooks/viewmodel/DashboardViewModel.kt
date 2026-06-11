package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.remote.DashboardResponse
import co.edu.cecar.smartbooks.data.model.VentaResponse
import co.edu.cecar.smartbooks.data.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(
        val contadores: DashboardResponse,
        val ultimasVentas: List<VentaResponse>,
        val ventasDelMesCount: Int,
        val ingresosDelMesSum: Double
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val dashboardRepository = DashboardRepository()
    private val context = application.applicationContext

    var uiState by mutableStateOf<DashboardUiState>(DashboardUiState.Loading)
        private set

    fun cargarDatosDashboard() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading

            try {
                val token = TokenManager.getToken(context)
                if (token.isNullOrBlank()) {
                    uiState = DashboardUiState.Error("Sesión inválida.")
                    return@launch
                }
                val contadoresDeferred = async { dashboardRepository.obtenerDatosDashboard(token) }
                val ventasDeferred = async { dashboardRepository.obtenerVentas(token) }

                val contadoresResult = contadoresDeferred.await()
                val ventasResult = ventasDeferred.await()

                val ventasValidas = ventasResult.filterNotNull()

                val mesActualStr = try {
                    SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
                } catch (e: Exception) {
                    ""
                }

                val ventasFiltradasMes = ventasValidas.filter { venta ->
                    val fechaVenta = venta.fecha
                    !fechaVenta.isNullOrBlank() && fechaVenta.startsWith(mesActualStr)
                }

                val totalVentasMesCalculado = ventasFiltradasMes.size

                val totalIngresosMesCalculado = ventasFiltradasMes.sumOf { venta ->
                    venta.total ?: 0.0
                }

                val ventasRecientes = ventasValidas.reversed().take(3)

                uiState = DashboardUiState.Success(
                    contadores = contadoresResult,
                    ultimasVentas = ventasRecientes,
                    ventasDelMesCount = totalVentasMesCalculado,
                    ingresosDelMesSum = totalIngresosMesCalculado
                )

            } catch (error: Exception) {
                error.printStackTrace()
                uiState = DashboardUiState.Error("Error al procesar los datos de SmartBooks: ${error.localizedMessage}")
            }
        }
    }
}