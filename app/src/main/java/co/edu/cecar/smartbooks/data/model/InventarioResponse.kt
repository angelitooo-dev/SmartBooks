package co.edu.cecar.smartbooks.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventarioResponse(
    @SerialName("lote") val lote: Int? = null,
    @SerialName("cantidadIngresada") val cantidadIngresada: Int? = null,
    @SerialName("cantidadVendida") val cantidadVendida: Int? = null,
    @SerialName("stockDisponible") val stockDisponible: Int? = null,

    // Las llaves reales del servidor descubiertas en la auditoría visual:
    @SerialName("idLibro") val idLibro: Int? = null,
    @SerialName("nombreLibro") val nombreLibro: String? = null,
    @SerialName("nivelLibro") val nivelLibro: String? = null,
    @SerialName("edicionLibro") val edicionLibro: String? = null,
    @SerialName("tipoLibro") val tipoLibro: String? = null,

    // Variable virtual limpia para conectar con la interfaz de usuario
    var libroNavigationDetalle: LibroNavigationDetalle? = null
)

@Serializable
data class LibroNavigationDetalle(
    val nombre: String? = null,
    val nivel: String? = null,
    val edicion: String? = null,
    val tipo: String? = null
)