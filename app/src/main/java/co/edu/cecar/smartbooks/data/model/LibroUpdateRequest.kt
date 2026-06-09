package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LibroUpdateRequest(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("nivel") val nivel: String,
    @SerialName("tipo") val tipo: Int,
    @SerialName("edicion") val edicion: String
)