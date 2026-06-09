package co.edu.cecar.smartbooks.data.repository

import android.util.Log
import co.edu.cecar.smartbooks.data.remote.InventarioResponse
import co.edu.cecar.smartbooks.data.remote.LibroNavigationDetalle
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

class InventariosRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun obtenerDatosInventario(token: String, lote: Int? = null): List<InventarioResponse> = coroutineScope {
        val urlInventario = if (lote != null) {
            "https://api.smartbooks.cecar.cloud/api/Inventarios?lote=$lote"
        } else {
            "https://api.smartbooks.cecar.cloud/api/Inventarios"
        }

        val tokenFormateado = if (token.trim().startsWith("Bearer ", ignoreCase = true)) {
            token.trim()
        } else {
            "Bearer ${token.trim()}"
        }

        return@coroutineScope try {
            val response = client.get(urlInventario) {
                header(HttpHeaders.Authorization, tokenFormateado)
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                val lista = response.body<List<InventarioResponse>>()

                // Asignamos directamente los valores internos del JSON a la interfaz
                lista.forEach { inventario ->
                    val tipoTexto = when (inventario.tipoLibro?.trim()) {
                        "0" -> "StudentsBook"
                        "1" -> "Workbook"
                        "2" -> "TeacherBook"
                        else -> inventario.tipoLibro ?: "Regular"
                    }

                    inventario.libroNavigationDetalle = LibroNavigationDetalle(
                        nombre = inventario.nombreLibro ?: "Sin Nombre",
                        nivel = inventario.nivelLibro ?: "-",
                        edicion = inventario.edicionLibro ?: "-",
                        tipo = tipoTexto
                    )
                }
                lista
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("REPOSITORY_INV", "Error obteniendo inventario", e)
            emptyList()
        }
    }
}