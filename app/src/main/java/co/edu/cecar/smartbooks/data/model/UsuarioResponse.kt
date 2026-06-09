package co.edu.cecar.smartbooks.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class UsuarioResponse(
    val id: Int? = null,
    val identificacion: String? = null,
    val nombres: String? = null,
    val email: String? = null,
    val rol: JsonElement? = null,
    val activo: Boolean? = true
)

@Serializable
data class RegisterUsuarioDto(
    @SerialName("Identificacion") val identificacion: String,
    @SerialName("Nombres") val nombres: String,
    @SerialName("Email") val email: String,
    @SerialName("Password") val password: String,
    @SerialName("Rol") val rol: Int
)

@Serializable
data class UpdateUsuarioDto(
    @SerialName("Nombres") val nombres: String,
    @SerialName("Email") val email: String,
    @SerialName("Rol") val rol: Int,
    @SerialName("Activo") val activo: Boolean
)