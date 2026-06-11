package co.edu.cecar.smartbooks.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventarioResponse(
    @SerialName("lote") val lote: Int? = null,
    @SerialName("cantidadIngresada") val cantidadIngresada: Int? = null,
    @SerialName("cantidadVendida") val cantidadVendida: Int? = null,
    @SerialName("stockDisponible") val stockDisponible: Int? = null,
    @SerialName("idLibro") val idLibro: Int? = null,
    @SerialName("nombreLibro") val nombreLibro: String? = null,
    @SerialName("nivelLibro") val nivelLibro: String? = null,
    @SerialName("edicionLibro") val edicionLibro: String? = null,
    @SerialName("tipoLibro") val tipoLibro: String? = null,

    var libroNavigationDetalle: LibroNavigationDetalle? = null
)

@Serializable
data class LibroNavigationDetalle(
    val nombre: String? = null,
    val nivel: String? = null,
    val edicion: String? = null,
    val tipo: String? = null
)