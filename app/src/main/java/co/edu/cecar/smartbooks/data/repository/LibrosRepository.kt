package co.edu.cecar.smartbooks.data.repository

import co.edu.cecar.smartbooks.data.model.LibroResponse
import co.edu.cecar.smartbooks.data.model.LibroUpdateRequest
import co.edu.cecar.smartbooks.data.model.LibroCreateRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class LibrosRepository {

    private val baseUrl = "https://api.smartbooks.cecar.cloud/api/Libros"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private fun formatToken(token: String): String =
        if (token.trim().startsWith("Bearer ", ignoreCase = true)) token.trim() else "Bearer ${token.trim()}"

    suspend fun obtenerLibros(token: String): List<LibroResponse> {
        val response = client.get(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw Exception("Error al cargar libros (${response.status.value})")
        }
    }
    suspend fun actualizarLibro(token: String, id: Int, request: LibroUpdateRequest): Boolean {
        val response = client.put("$baseUrl/$id") {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.isSuccess()
    }

    suspend fun crearLibro(token: String, request: LibroCreateRequest): Boolean {
        val response = client.post(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.isSuccess()
    }

    suspend fun buscarLibros(token: String, nombre: String?, nivel: String?, tipo: Int?, edicion: String?): List<LibroResponse> {
        val response = client.get(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            nombre?.let { parameter("Nombre", it) }
            nivel?.let { parameter("Nivel", it) }
            tipo?.let { parameter("Tipo", it) }
            edicion?.let { parameter("Edicion", it) }
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw Exception("Error al buscar libros (${response.status.value})")
        }
    }

}
