package co.edu.cecar.smartbooks.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class DashboardResponse(
    @SerialName("mensajeBienvenida") val mensajeBienvenida: String? = null,
    @SerialName("totalClientes") val totalClientes: Int = 0,
    @SerialName("totalLibros") val librosRegistrados: Int = 0,
    @SerialName("totalVentas") val ventasMes: Int = 0,
    @SerialName("totalIngresos") val ingresosMes: Long = 0
)