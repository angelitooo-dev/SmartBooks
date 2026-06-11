package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class IngresoCreate(
    @SerialName("libroId")
    val libroId: Int,
    @SerialName("unidades")
    val unidades: Int,
    @SerialName("lote")
    val lote: Int,
    @SerialName("valorCompra")
    val valorCompra: Double,
    @SerialName("valorVentaPublico")
    val valorVentaPublico: Double
)