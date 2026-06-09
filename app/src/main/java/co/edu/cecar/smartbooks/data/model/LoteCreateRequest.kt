package co.edu.cecar.smartbooks.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoteCreateRequest(
    @SerialName("lote") val lote: Int
)

