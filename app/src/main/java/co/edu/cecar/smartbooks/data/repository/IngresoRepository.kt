package co.edu.cecar.smartbooks.data.repository

import co.edu.cecar.smartbooks.core.network.HttpClientProvider
import co.edu.cecar.smartbooks.data.model.IngresoCreate
import co.edu.cecar.smartbooks.data.model.IngresoResponse
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class IngresosRepository {
    private val client = HttpClientProvider.client
    private val BASE_URL = "https://api.smartbooks.cecar.cloud"

    suspend fun obtenerIngresos(
        desde: String? = null,
        hasta: String? = null,
        lote: Int? = null,
        libroId: Int? = null
    ): Result<List<IngresoResponse>> {
        return try {
            val response = client.get("$BASE_URL/api/Ingresos") {
                url {
                    desde?.let { parameters.append("Desde", it) }
                    hasta?.let { parameters.append("Hasta", it) }
                    lote?.let { parameters.append("Lote", it.toString()) }
                    libroId?.let { parameters.append("libroId", it.toString()) }
                }
            }
            if (response.status.isSuccess()) {
                Result.success(response.body<List<IngresoResponse>>())
            } else {
                Result.failure(Exception(response.bodyAsText()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarIngreso(ingreso: IngresoCreate): Result<String> {
        return try {
            val response = client.post("$BASE_URL/api/Ingresos") {
                contentType(ContentType.Application.Json)
                setBody(ingreso)
            }
            val text = response.bodyAsText()
            if (response.status.isSuccess()) {
                Result.success(text)
            } else {
                Result.failure(Exception(text))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}