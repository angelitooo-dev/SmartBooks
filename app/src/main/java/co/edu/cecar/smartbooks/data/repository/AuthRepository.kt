package co.edu.cecar.smartbooks.data.repository

import co.edu.cecar.smartbooks.core.network.HttpClientProvider
import co.edu.cecar.smartbooks.data.model.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthRepository {
    private val client = HttpClientProvider.client
    private val BASE_URL = "https://api.smartbooks.cecar.cloud"
    suspend fun login(correo: String, contrasena: String): Result<LoginResponse> {
        return try {
            val response = client.post("$BASE_URL/api/Seguridad/iniciar-sesion") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email = correo, password = contrasena))
            }
            val text = response.bodyAsText()
            if (text.trim().startsWith("<")) {
                return Result.failure(Exception("Error de enrutamiento en el servidor (${response.status.value})."))
            }
            if (response.status.isSuccess()) {
                Result.success(response.body<LoginResponse>())
            } else {
                Result.failure(Exception(text))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verificarCorreo(token: String): Result<String> {
        return try {
            val response = client.get("$BASE_URL/api/Seguridad/verificar-correo") {
                parameter("token", token)
            }
            if (response.status.isSuccess()) Result.success(response.bodyAsText())
            else Result.failure(Exception(response.bodyAsText()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun solicitarRestablecimiento(correo: String): Result<String> {
        return try {
            val response = client.post("$BASE_URL/api/Seguridad/solicitar-restablecimiento") {
                contentType(ContentType.Application.Json)
                setBody(RequestPasswordResetDto(email = correo))
            }
            if (response.status.isSuccess()) Result.success(response.bodyAsText())
            else Result.failure(Exception(response.bodyAsText()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restablecerContrasena(codigo: String, nuevaPass: String): Result<String> {
        return try {
            val response = client.post("$BASE_URL/api/Seguridad/restablecer-contrasena") {
                contentType(ContentType.Application.Json)
                setBody(ResetPasswordDto(codigo = codigo, newPassword = nuevaPass))
            }
            if (response.status.isSuccess()) Result.success(response.bodyAsText())
            else Result.failure(Exception(response.bodyAsText()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}