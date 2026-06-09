package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LibroCreateRequest(
    val nombre: String,
    val nivel: String,
    val tipo: Int,
    val edicion: String,
    val unidades: Int,
    val lote: Int,
    val valorCompra: Int,
    val valorVentaPublico: Int
)


