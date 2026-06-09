package co.edu.cecar.smartbooks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordDto(
    val codigo: String,
    val newPassword: String
)