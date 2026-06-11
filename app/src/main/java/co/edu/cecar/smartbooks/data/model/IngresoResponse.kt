package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class IngresoResponse(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("lote")
    val lote: Int? = null,
    @SerialName("unidades")
    val unidades: Int? = null,
    @SerialName("valorCompra")
    val valorCompra: Double? = null,
    @SerialName("valorVentaPublico")
    val valorVentaPublico: Double? = null,
    @SerialName("fechaRegistro")
    val fechaRegistro: String? = null,
    @SerialName("libroId")
    val libroId: Int? = null
)