package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LoginResponse(
    @SerialName("token")
    val token: String,
)