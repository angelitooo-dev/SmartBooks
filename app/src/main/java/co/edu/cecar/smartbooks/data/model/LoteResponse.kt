package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoteResponse(
    // Apuntamos a "codigo" que es la palabra real que vimos en tu JSON Input
    @SerialName("codigo") val lote: Int,

    // Apuntamos a "actual" que es el booleano real
    @SerialName("actual") val actual: Boolean
)

