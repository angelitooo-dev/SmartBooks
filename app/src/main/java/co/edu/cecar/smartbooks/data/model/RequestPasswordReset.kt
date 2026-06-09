package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestPasswordResetDto(
    val email: String
)