package co.edu.cecar.smartbooks.data.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import co.edu.cecar.smartbooks.core.network.HttpClientProvider
import co.edu.cecar.smartbooks.data.model.*

class VentasRepository {

    private val baseUrl = "https://api.smartbooks.cecar.cloud/api/Ventas"
    private val client = HttpClientProvider.client

    private fun formatToken(token: String): String =
        if (token.startsWith("Bearer ")) token else "Bearer $token"

    // GET /api/Ventas
    suspend fun obtenerVentas(token: String): List<VentaResponse> {
        val response = client.get(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            accept(ContentType.Application.Json)
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw Exception("Error al cargar el historial de ventas (${response.status.value})")
        }
    }

    // GET /api/Ventas/{id}
    suspend fun obtenerDetalleVenta(token: String, id: Int): VentaResponse {
        val response = client.get("$baseUrl/$id") {
            header(HttpHeaders.Authorization, formatToken(token))
            accept(ContentType.Application.Json)
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw Exception("No se pudo recuperar el detalle de la venta")
        }
    }

    // POST /api/Ventas
    suspend fun registrarVenta(token: String, request: RegistrarVentaDto): Boolean {
        val response = client.post(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.isSuccess()
    }
}