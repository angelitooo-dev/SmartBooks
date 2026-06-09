package co.edu.cecar.smartbooks.data.repository

import android.util.Log
import co.edu.cecar.smartbooks.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ClientesRepository {

    private val baseUrl = "https://api.smartbooks.cecar.cloud/api/Clientes"

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

    suspend fun obtenerClientes(token: String, nombres: String? = null): List<ClienteResponse> {
        val response = client.get(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            nombres?.let { parameter("nombres", it) }
        }
        val responseBody = response.bodyAsText()
        Log.d("ClientesRepo", "GET /Clientes status: ${response.status.value}")
        Log.d("ClientesRepo", "GET /Clientes body: $responseBody")

        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw Exception("Error al cargar clientes (${response.status.value})")
        }
    }

    suspend fun obtenerClientePorIdentificacion(token: String, identificacion: String): ClienteResponse? {
        val response = client.get("$baseUrl/$identificacion") {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
        }
        val responseBody = response.bodyAsText()
        Log.d("ClientesRepo", "GET /Clientes/$identificacion status: ${response.status.value}")
        Log.d("ClientesRepo", "GET /Clientes/$identificacion body: $responseBody")

        return if (response.status.isSuccess()) {
            response.body()
        } else if (response.status == HttpStatusCode.NotFound) {
            null
        } else {
            throw Exception("Error al buscar identificación (${response.status.value})")
        }
    }
    suspend fun crearCliente(token: String, request: ClienteCreateRequest): Pair<Boolean, String?> {
        val response = client.post(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val responseBody = response.bodyAsText()
        Log.d("ClientesRepo", "POST /Clientes status: ${response.status.value}")
        Log.d("ClientesRepo", "POST /Clientes body: $responseBody")

        return if (response.status.isSuccess()) {
            Pair(true, null)
        } else {
            Pair(false, responseBody)
        }
    }
    suspend fun actualizarCliente(token: String, identificacion: String, request: ClienteUpdateRequest): Pair<Boolean, String?> {
        val response = client.put("$baseUrl/$identificacion") {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val responseBody = response.bodyAsText()
        Log.d("ClientesRepo", "PUT /Clientes/$identificacion status: ${response.status.value}")
        Log.d("ClientesRepo", "PUT /Clientes/$identificacion body: $responseBody")

        return if (response.status.isSuccess()) {
            Pair(true, null)
        } else {
            Pair(false, responseBody)
        }
    }
}




