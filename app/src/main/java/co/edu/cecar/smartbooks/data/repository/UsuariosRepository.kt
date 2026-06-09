package co.edu.cecar.smartbooks.data.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import co.edu.cecar.smartbooks.core.network.HttpClientProvider
import co.edu.cecar.smartbooks.data.remote.RegisterUsuarioDto
import co.edu.cecar.smartbooks.data.remote.UpdateUsuarioDto
import co.edu.cecar.smartbooks.data.remote.UsuarioResponse

class UsuariosRepository {

    private val baseUrl = "https://api.smartbooks.cecar.cloud/api/Usuarios"
    private val client = HttpClientProvider.client

    private fun formatToken(token: String): String =
        if (token.trim().startsWith("Bearer ", ignoreCase = true)) token.trim() else "Bearer ${token.trim()}"

    suspend fun obtenerUsuarios(token: String, nombres: String? = null, rol: Int? = null): List<UsuarioResponse> {
        val response = client.get(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            header(HttpHeaders.Accept, "application/json")
            nombres?.let { parameter("nombres", it) }
            rol?.let { parameter("rol", it) }
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw Exception("API Error ${response.status.value}")
        }
    }

    suspend fun obtenerUsuarioPorId(token: String, id: Int): UsuarioResponse {
        val response = client.get("$baseUrl/$id") {
            header(HttpHeaders.Authorization, formatToken(token))
            header(HttpHeaders.Accept, "application/json")
        }
        return response.body()
    }

    suspend fun registrarUsuario(token: String, request: RegisterUsuarioDto): Boolean {
        val response = client.post(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.isSuccess()
    }

    suspend fun actualizarUsuario(token: String, id: Int, request: UpdateUsuarioDto): Boolean {
        val response = client.put("$baseUrl/$id") {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.isSuccess()
    }

    suspend fun cambiarEstadoUsuario(token: String, id: Int): Boolean {
        val response = client.patch("$baseUrl/$id/estado") {
            header(HttpHeaders.Authorization, formatToken(token))
            header(HttpHeaders.Accept, "application/json")
        }
        return response.status.isSuccess()
    }

    suspend fun obtenerPerfilAutenticado(token: String): UsuarioResponse {
        val response = client.get("$baseUrl/perfil") {
            header(HttpHeaders.Authorization, formatToken(token))
            header(HttpHeaders.Accept, "application/json")
        }
        return response.body()
    }
}