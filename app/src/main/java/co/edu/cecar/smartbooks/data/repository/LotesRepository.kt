package co.edu.cecar.smartbooks.data.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import co.edu.cecar.smartbooks.core.network.HttpClientProvider
import co.edu.cecar.smartbooks.data.model.*

class LotesRepository {

    private val baseUrl = "https://api.smartbooks.cecar.cloud/api/Lotes"
    private val client = HttpClientProvider.client

    private fun formatToken(token: String): String =
        if (token.startsWith("Bearer ")) token else "Bearer $token"

    suspend fun obtenerLotes(token: String): List<LoteResponse> {
        val response = client.get(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            accept(ContentType.Application.Json)
        }
        return response.body()
    }

    suspend fun crearLote(token: String, numero: Int): Boolean {
        val response = client.post(baseUrl) {
            header(HttpHeaders.Authorization, formatToken(token))
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(LoteCreateRequest(numero))
        }
        return response.status.isSuccess()
    }
}