package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VentaResponse(
    @SerialName("id") val id: Int? = null,
    @SerialName("fecha") val fecha: String? = null,
    @SerialName("identificacionCliente") val identificacionCliente: String? = null,
    @SerialName("cliente") val cliente: String? = null,
    @SerialName("clienteNombre") val clienteNombre: String? = null,
    @SerialName("total") val total: Double? = 0.0,
    @SerialName("numeroComprobante") val numeroComprobante: String? = null,
    @SerialName("observaciones") val observaciones: String? = null,
    @SerialName("items") val items: List<VentaItemResponse>? = emptyList()
)

@Serializable
data class VentaItemResponse(
    @SerialName("libroId") val libroId: Int? = null,
    @SerialName("nombreLibro") val nombreLibro: String? = null,
    @SerialName("lote") val lote: Int? = null,
    @SerialName("cantidad") val cantidad: Int? = 0,
    @SerialName("precioUnitario") val precioUnitario: Double? = 0.0
)

@Serializable
data class RegistrarVentaDto(
    @SerialName("identificacionCliente") val identificacionCliente: String,
    @SerialName("numeroComprobante") val numeroComprobante: String,
    @SerialName("observaciones") val observaciones: String,
    @SerialName("items") val items: List<RegistrarVentaItemDto>
)

@Serializable
data class RegistrarVentaItemDto(
    @SerialName("libroId") val libroId: Int,
    @SerialName("lote") val lote: Int,
    @SerialName("cantidad") val cantidad: Int
)

// Estructura limpia para gestionar el estado de la cesta en Compose
data class CarritoItem(
    val libroId: Int,
    val libroTitulo: String,
    val lote: Int,
    val cantidad: Int,
    val precioUnitario: Double
)