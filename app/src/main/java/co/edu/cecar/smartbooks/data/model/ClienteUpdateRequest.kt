package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ClienteUpdateRequest(
    val nombres: String,
    val email: String,
    val celular: String,
    val fechaNacimiento: String
)
