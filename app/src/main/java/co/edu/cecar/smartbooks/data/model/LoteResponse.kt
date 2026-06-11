package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoteResponse(
    @SerialName("codigo")
    val lote: Int,


    @SerialName("actual")
    val actual: Boolean
)

