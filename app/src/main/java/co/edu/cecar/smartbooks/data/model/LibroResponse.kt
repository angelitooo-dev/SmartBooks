package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable


@Serializable
data class LibroResponse(
    val id: Int,
    val nombre: String,
    val nivel: String,
    val tipo: String,
    val edicion: String,
    val unidades: Int? = 0,
    val lote: Int? = 0,
    val valorCompra: Int? = 0,
    val valorVentaPublico: Int? = 0
)



