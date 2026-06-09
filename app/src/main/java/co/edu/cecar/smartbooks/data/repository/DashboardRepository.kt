package co.edu.cecar.smartbooks.data.repository

import co.edu.cecar.smartbooks.data.remote.DashboardResponse
import co.edu.cecar.smartbooks.data.model.VentaResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class DashboardRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private val baseUrl = "https://api.smartbooks.cecar.cloud/api"

    suspend fun obtenerDatosDashboard(token: String): DashboardResponse {
        val url = "$baseUrl/Dashboard"
        val tokenFormateado = formatearBearerToken(token)

        val response = client.get(url) {
            header(HttpHeaders.Authorization, tokenFormateado)
            contentType(ContentType.Application.Json)
        }

        if (response.status.isSuccess()) {
            return response.body<DashboardResponse>()
        } else {
            throw Exception("Error Dashboard: ${response.status.value}")
        }
    }

    suspend fun obtenerVentas(token: String): List<VentaResponse> {
        val url = "$baseUrl/Ventas"
        val tokenFormateado = formatearBearerToken(token)

        val response = client.get(url) {
            header(HttpHeaders.Authorization, tokenFormateado)
            contentType(ContentType.Application.Json)
        }

        if (response.status.isSuccess()) {
            // Si el servidor devuelve null, nos aseguramos de retornar una lista vacía para evitar crashes
            return response.body<List<VentaResponse>?>() ?: emptyList()
        } else {
            throw Exception("Error Ventas: ${response.status.value}")
        }
    }

    private fun formatearBearerToken(token: String): String {
        return if (token.trim().startsWith("Bearer ", ignoreCase = true)) {
            token.trim()
        } else {
            "Bearer ${token.trim()}"
        }
    }
}